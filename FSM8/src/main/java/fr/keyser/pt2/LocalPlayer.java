package fr.keyser.pt2;

import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt2.buildings.Building;
import fr.keyser.pt2.effects.CardTargets;
import fr.keyser.pt2.effects.ChoosenTargets;
import fr.keyser.pt2.effects.EffectLog;
import fr.keyser.pt2.effects.Target;
import fr.keyser.pt2.effects.TargetableEffect;
import fr.keyser.pt2.units.Unit;
import fr.keyser.pt2.view.FullLocalPlayerView;

public class LocalPlayer {

    private final LocalBoard localBoard;

    private List<Unit> selecteds;

    private List<Unit> selectables;

    private List<Building> buildings;

    private PlayerStats previous = new PlayerStats();

    private PlayerStats current = previous;

    private Set<Integer> activated = new HashSet<>();

    public LocalPlayer(LocalBoard localBoard) {
	this.localBoard = localBoard;
    }

    public List<EffectLog> activateDeploy(CardPosition position, ChoosenTargets targets) {
	Slot slot = localBoard.getSlot(position);
	Card card = slot.getCard().get();
	activated.add(card.getId());

	Stream<TargetableEffect> effects = card.getDeployEffects();

	List<EffectLog> logs = effects.flatMap(e -> e.apply(slot, targets).stream()).collect(toList());

	gain(card.getDeployGoldGain().getValue(), card.getDeployLegend().getValue());
	return logs;
    }

    public List<CardTargets> deployEffects() {
	return targets(Card::getDeployEffects);
    }

    public List<CardTargets> ageEffects() {
	return targets(Card::getAgeEffects);
    }

    private List<CardTargets> targets(Function<Card, Stream<TargetableEffect>> mapper) {
	return localBoard.all().stream().flatMap(s -> targets(s, mapper)).collect(toList());
    }

    private Stream<CardTargets> targets(Slot s, Function<Card, Stream<TargetableEffect>> mapper) {
	Card card = s.getCard().get();
	if (card != null && !activated.contains(card.getId())) {
	    CardPosition position = s.getCardPosition();
	    List<Target> targets = mapper.apply(card).flatMap(x -> x.targets(s)).collect(toList());
	    if (!targets.isEmpty()) {
		return Stream.of(new CardTargets(position, targets));
	    }
	}

	return Stream.empty();
    }

    public void agePhase() {
	activated.clear();
    }

    public void endAgePhase() {
	activated.clear();
	gain(current.getAgeGoldGain(), current.getAgeLegend());
    }

    public void deployPhase() {

	resetDeployGain();

	PlayerStats stats = localBoard.stats();

	stats.setGold(previous.getGold());
	stats.setLegend(previous.getLegend());

	previous = current;
	current = stats;
	activated.clear();
    }

    private void resetDeployGain() {
	localBoard.getUnits().forEach(u -> {
	    u.getDeployGoldGain().set(0);
	    u.getDeployLegend().set(0);
	});
    }

    public void endDeployPhase() {
	activated.clear();
	resetDeployGain();
    }

    private void gain(int goldGain, int legendGain) {
	current.setGold(current.getGold() + goldGain);
	current.setLegend(current.getLegend() + legendGain);
    }

    public void payPhase() {
	gain(current.getPayGoldGain(), current.getPayLegend());
    }

    public FullLocalPlayerView viewAtStart() {
	return previous.view();
    }

    public FullLocalPlayerView view() {
	return current.view();
    }

    public void warPhase() {
	gain(current.getWarGoldGain(), current.getWarLegend());
    }
}
