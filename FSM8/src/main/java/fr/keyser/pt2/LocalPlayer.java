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
import fr.keyser.pt2.units.Unit;

public class LocalPlayer {

    private final LocalBoard localBoard;

    private List<Unit> selecteds;

    private List<String> selectables;

    private List<Building> buildings;

    private PlayerStats previous = new PlayerStats();

    private PlayerStats current = previous;

    private Set<Integer> activated = new HashSet<>();

    public LocalPlayer(LocalBoard localBoard) {
	this.localBoard = localBoard;
    }

    private Card getCard(CardPosition position) {
	Slot slot = localBoard.getSlot(position);
	return slot.getCard().get();
    }

    public List<EffectLog> activateDeploy(CardPosition position, ChoosenTargets targets) {
	Card card = getCard(position);

	List<EffectLog> logs = activate(card, targets, card.getDeployEffects());

	gain(card.getDeployGoldGain().getValue(), card.getDeployLegend().getValue());
	return logs;
    }

    private List<EffectLog> activate(Card card, ChoosenTargets targets, Stream<TargetableEffect> effects) {
	activated.add(card.getId());

	Slot slot = localBoard.getSlot(card.getPosition());

	return effects.flatMap(e -> e.apply(slot, targets).stream()).collect(toList());
    }

    public List<EffectLog> activeAge(CardPosition position, ChoosenTargets targets) {
	Card card = getCard(position);
	return activate(card, targets, card.getAgeEffects());
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

    public void warPhase() {
	gain(current.getWarGoldGain(), current.getWarLegend());
    }
}
