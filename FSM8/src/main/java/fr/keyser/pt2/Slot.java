package fr.keyser.pt2;

import java.util.function.Function;
import java.util.stream.Stream;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt2.prop.BoolSupplier;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.DirtySupplier;
import fr.keyser.pt2.prop.IntSupplier;
import fr.keyser.pt2.prop.MutableProp;

public final class Slot {

    public static <T, R> R apply(T o, Function<T, R> mapper) {
	if (o != null)
	    return mapper.apply(o);
	else
	    return null;
    }

    private static IntSupplier effectiveCombat(Card card) {
	return card.getCombat().when(card.getMayCombat());
    }

    private final LocalBoard board;

    private final MutableProp<Card> card = new MutableProp<>();

    private final CardPosition cardPosition;

    private final IntSupplier food = mapInt(Card::getFood);

    private final IntSupplier wood = mapInt(Card::getWood);

    private final IntSupplier crystal = mapInt(Card::getCrystal);

    private final IntSupplier effectiveCombat = mapInt(Slot::effectiveCombat);

    private final IntSupplier combat = mapInt(Card::getCombat);

    private final IntSupplier cost = mapInt(Card::getCost);

    private final IntSupplier age = mapInt(Card::getAge);

    private final IntSupplier deployLegend = mapInt(Card::getDeployLegend);

    private final IntSupplier deployGoldGain = mapInt(Card::getDeployGoldGain);

    private final IntSupplier warLegend = mapInt(Card::getWarLegend);

    private final IntSupplier warGoldGain = mapInt(Card::getWarGoldGain);

    private final IntSupplier payLegend = mapInt(Card::getPayLegend);

    private final IntSupplier payGoldGain = mapInt(Card::getPayGoldGain);

    private final IntSupplier ageLegend = mapInt(Card::getAgeLegend);

    private final IntSupplier ageGoldGain = mapInt(Card::getAgeGoldGain);

    private final BoolSupplier willDie = mapBool(Card::getWillDie);

    private final BoolSupplier willLive = mapBool(Card::getWillLive);

    private final IntSupplier dyingAgeToken = mapInt(Card::getDyingAgeToken);

    private final IntSupplier buildLevel = mapInt(Card::getBuildLevel);

    private final BoolSupplier buildLevel2 = buildLevel.gte(ConstInt.TWO);

    public Slot(LocalBoard board, CardPosition cardPosition) {
	this.board = board;
	this.cardPosition = cardPosition;
    }

    public Stream<CardMemento> memento() {
	Card card = this.card.get();
	if (card != null)
	    return Stream.of(card.getMemento());
	else
	    return Stream.empty();
    }

    <T> T get(Function<LocalBoard, T> accessor) {
	return accessor.apply(board);
    }

    public IntSupplier getAge() {
	return age;
    }

    public IntSupplier getAgeGoldGain() {
	return ageGoldGain;
    }

    public IntSupplier getAgeLegend() {
	return ageLegend;
    }

    public LocalBoard getBoard() {
	return board;
    }

    public IntSupplier getBuildLevel() {
	return buildLevel;
    }

    public BoolSupplier getBuildLevel2() {
	return buildLevel2;
    }

    public DirtySupplier<Card> getCard() {
	return card;
    }

    public CardPosition getCardPosition() {
	return cardPosition;
    }

    public IntSupplier getCombat() {
	return combat;
    }

    public IntSupplier getCrystal() {
	return crystal;
    }

    public IntSupplier getDeployGoldGain() {
	return deployGoldGain;
    }

    public IntSupplier getDeployLegend() {
	return deployLegend;
    }

    public IntSupplier getDyingAgeToken() {
	return dyingAgeToken;
    }

    public IntSupplier getEffectiveCombat() {
	return effectiveCombat;
    }

    public IntSupplier getFood() {
	return food;
    }

    public IntSupplier getPayGoldGain() {
	return payGoldGain;
    }

    public IntSupplier getPayLegend() {
	return payLegend;
    }

    public IntSupplier getWarGoldGain() {
	return warGoldGain;
    }

    public IntSupplier getWarLegend() {
	return warLegend;
    }

    public BoolSupplier getWillDie() {
	return willDie;
    }

    public BoolSupplier getWillLive() {
	return willLive;
    }

    public IntSupplier getWood() {
	return wood;
    }

    private BoolSupplier mapBool(Function<Card, BoolSupplier> mapper) {
	return this.card.mapBool(mapper);
    }

    private IntSupplier mapInt(Function<Card, IntSupplier> mapper) {
	return this.card.mapInt(mapper);
    }

    public void play(Card card) {
	setCard(card, true);
	card.deploy();
    }

    public void removeCard() {
	setCard(null, true);
    }

    public void moveCard(Card card) {
	setCard(card, false);
    }

    private void setCard(Card card, boolean triggerOldCard) {

	if (triggerOldCard) {
	    Card oldCard = this.card.get();
	    if (oldCard != null) {
		oldCard.setPosition(null);
		oldCard.setBoard(null);
	    }
	}

	this.card.set(card);

	if (card != null) {
	    card.setPosition(cardPosition);
	    card.setBoard(board);
	}
    }

    public IntSupplier getCost() {
	return cost;
    }
}
