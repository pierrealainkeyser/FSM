package fr.keyser.pt2;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt2.effects.CardTargets;
import fr.keyser.pt2.prop.IntSupplier;
import fr.keyser.pt2.units.Unit;

public class LocalPlayer {

    private Set<CardPosition> activated = new HashSet<>();

    private PlayerView current;

    private final LocalBoard localBoard;

    private PlayerView previous;

    private int gold;

    private int legend;

    private List<String> currentDraft = new ArrayList<>();

    private List<Unit> hand = new ArrayList<>();

    private final LocalGame game;

    public LocalPlayer(LocalGame game, LocalBoard localBoard) {
	this.game = game;
	this.localBoard = localBoard;
	this.gold = 2;
	snapshotPrivateView();
	makePrivateInfoPublic();
    }

    public Optional<Unit> handById(int id) {
	return hand.stream().filter(u -> u.getId() == id).findFirst();
    }

    public void deployUnit(Unit unit, CardPosition to) {
	getSlot(to).play(unit);
	hand.remove(unit);
    }

    private List<EffectLog> activate(Card card, ChoosenTargets targets, Stream<TargetableEffect> effects) {
	activated.add(card.getPosition());

	Slot slot = localBoard.getSlot(card.getPosition());

	List<EffectLog> effect = effects.flatMap(e -> e.apply(slot, targets).stream()).collect(toList());
	snapshotPrivateView();
	return effect;
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

	snapshotPrivateView();
	activated.clear();
    }

    public void endAgePhase() {
	activated.clear();
	gain(current.getAge());

	// remove dying units
	localBoard.getUnits().filter(u -> u.getWillDie().get()).forEach(u -> u.setPosition(null));

	localBoard.getUnits().forEach(u -> u.getSimpleDyingProtection().set(false));

	makePrivateInfoPublic();

    }

    public void endDeployPhase() {
	activated.clear();
	gain(current.getDeploy());
	resetDeployGain();
	makePrivateInfoPublic();
    }

    void addToHand(String unitName) {
	currentDraft.add(unitName);
    }

    public void pick(int unitIndex) {
	String unitName = currentDraft.remove(unitIndex);
	hand.add(game.unit(unitName));
    }

    public void pickAndDiscard(int pickIndex, int discardIndex) {
	String picked = currentDraft.remove(pickIndex);
	if (discardIndex > pickIndex)
	    --discardIndex;

	String discarded = currentDraft.remove(discardIndex);
	hand.add(game.unit(picked));
	game.discard(discarded);
    }

    public void keep(int unitIndex) {
	Iterator<Unit> it = hand.iterator();
	boolean forceDiscard = false;
	int index = 0;
	while (it.hasNext()) {
	    Unit next = it.next();
	    if (forceDiscard || index != unitIndex) {
		game.discard(next.getName());
		it.remove();
		forceDiscard = true;
	    }
	    ++index;
	}
    }

    private void gain(GoldLegendGain gain) {
	gold += gain.getGold();
	legend += gain.getLegend();
	snapshotPrivateView();
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

    /**
     * TODO restore memento
     * 
     * @return
     */
    public PlayerMemento memento() {
	PlayerMemento p = new PlayerMemento();
	updateMemento(p);
	p.setHand(hand.stream().map(Unit::getName).collect(toList()));
	p.setCurrentDraft(currentDraft);
	return p;
    }

    private PlayerScoreMemento updateMemento(PlayerScoreMemento p) {
	p.setCards(localBoard.memento());
	p.setGold(gold);
	p.setLegend(legend);
	return p;
    }

    public void payPhase() {
	gain(current.getPay());
	makePrivateInfoPublic();
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

    public void setNeighbours(IntSupplier left, IntSupplier right) {
	localBoard.setNeighbours(left, right);
    }

    public void setNeighbours(LocalPlayer left, LocalPlayer right) {
	localBoard.setNeighbours(left.localBoard, right.localBoard);
    }

    private void snapshotPrivateView() {
	current = new PlayerView(localBoard.stats(), updateMemento(new PlayerScoreMemento()));
    }

    private List<CardTargets> targets(Function<Card, Stream<TargetableEffect>> mapper) {
	return localBoard.all().stream().flatMap(s -> targets(s, mapper)).collect(toList());
    }

    private Stream<CardTargets> targets(Slot s, Function<Card, Stream<TargetableEffect>> mapper) {
	Card card = s.getCard().get();
	if (card != null) {
	    CardPosition position = s.getCardPosition();
	    if (!activated.contains(position)) {
		List<Target> targets = mapper.apply(card).flatMap(x -> x.targets(s)).collect(toList());
		if (!targets.isEmpty()) {
		    return Stream.of(new CardTargets(position, targets));
		}
	    }
	}

	return Stream.empty();
    }

    public void warPhase() {
	gain(current.getWar());
	makePrivateInfoPublic();
    }

    protected List<String> getCurrentDraft() {
	return currentDraft;
    }

    protected void setCurrentDraft(List<String> currentHand) {
	this.currentDraft = currentHand;
    }

    public Slot getSlot(CardPosition position) {
	return localBoard.getSlot(position);
    }
}
