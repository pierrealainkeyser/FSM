package fr.keyser.pt2;

import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt2.effects.CardTargets;
import fr.keyser.pt2.effects.ChoosenTargets;

public class LocalPlayer {

    private Set<Integer> activated = new HashSet<>();

    private PlayerView current;

    private final LocalBoard localBoard;

    private PlayerView previous;

    public LocalPlayer(LocalBoard localBoard) {
	this.localBoard = localBoard;
	current = localBoard.stats(0, 0);
	makePrivateInfoPublic();
    }

    private List<EffectLog> activate(Card card, ChoosenTargets targets, Stream<TargetableEffect> effects) {
	activated.add(card.getId());

	Slot slot = localBoard.getSlot(card.getPosition());

	return effects.flatMap(e -> e.apply(slot, targets).stream()).collect(toList());
    }

    public List<EffectLog> activateDeploy(CardPosition position, ChoosenTargets targets) {
	Card card = getCard(position);

	List<EffectLog> logs = activate(card, targets, card.getDeployEffects());

	gain(card.getDeployGain());
	return logs;
    }

    public List<EffectLog> activeAge(CardPosition position, ChoosenTargets targets) {
	Card card = getCard(position);
	return activate(card, targets, card.getAgeEffects());
    }

    public List<CardTargets> ageEffects() {
	return targets(Card::getAgeEffects);
    }

    public void agePhase() {
	activated.clear();
    }

    public List<CardTargets> deployEffects() {
	return targets(Card::getDeployEffects);
    }

    public void deployPhase() {

	resetDeployGain();

	// creation des nouvelles stats
	current = localBoard.stats(previous.getGold(), previous.getLegend());
	activated.clear();
    }

    public void endAgePhase() {
	activated.clear();
	gain(current.getAgeGain());
    }

    public void endDeployPhase() {
	makePrivateInfoPublic();
	activated.clear();
	resetDeployGain();
    }

    private void gain(GoldLegendGain gain) {
	current.setGold(current.getGold() + gain.getGold());
	current.setLegend(current.getLegend() + gain.getLegend());
    }

    private Card getCard(CardPosition position) {
	Slot slot = localBoard.getSlot(position);
	return slot.getCard().get();
    }

    public PlayerView getPrivateView() {
	return current;
    }

    public PlayerView getPublicView() {
	return previous;
    }

    private void makePrivateInfoPublic() {
	previous = current;
    }

    public PlayerMemento memento() {
	return localBoard.memento(current.getGold(), current.getLegend());
    }

    public void payPhase() {
	gain(current.getPayGain());
    }

    private void resetDeployGain() {
	localBoard.getUnits().forEach(u -> {
	    u.getDeployGoldGain().set(0);
	    u.getDeployLegend().set(0);
	});
    }

    public void setNeighbour(LocalPlayer opponent) {
	localBoard.setNeighbour(opponent.localBoard);
    }

    public void setNeighbours(LocalPlayer left, LocalPlayer right) {
	localBoard.setNeighbours(left.localBoard, right.localBoard);
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

    public void warPhase() {
	gain(current.getWarGain());
    }
}
