package fr.keyser.pt2;

import java.util.function.Function;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt2.prop.BoolSupplier;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.IntSupplier;
import fr.keyser.pt2.prop.PlugableBool;
import fr.keyser.pt2.prop.PlugableInt;

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

    private Card card;

    private final CardPosition cardPosition;

    private final PlugableInt food = new PlugableInt();

    private final PlugableInt wood = new PlugableInt();

    private final PlugableInt crystal = new PlugableInt();

    private final PlugableInt effectiveCombat = new PlugableInt();

    private final PlugableInt combat = new PlugableInt();

    private final PlugableInt age = new PlugableInt();

    private final PlugableInt deployLegend = new PlugableInt();

    private final PlugableInt deployGoldGain = new PlugableInt();

    private final PlugableInt warLegend = new PlugableInt();

    private final PlugableInt warGoldGain = new PlugableInt();

    private final PlugableInt payLegend = new PlugableInt();

    private final PlugableInt payGoldGain = new PlugableInt();

    private final PlugableInt ageLegend = new PlugableInt();

    private final PlugableInt ageGoldGain = new PlugableInt();

    private final PlugableBool willDie = new PlugableBool();

    private final PlugableInt dyingAgeToken = new PlugableInt();

    private final PlugableInt buildLevel = new PlugableInt();

    private final BoolSupplier buildLevel2 = buildLevel.gte(ConstInt.TWO);

    public Slot(LocalBoard board, CardPosition cardPosition) {
	this.board = board;
	this.cardPosition = cardPosition;
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

    public IntSupplier getBuildLevel() {
	return buildLevel;
    }

    public BoolSupplier getBuildLevel2() {
	return buildLevel2;
    }

    public Card getCard() {
	return card;
    }

    public CardPosition getCardPosition() {
	return cardPosition;
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

    public IntSupplier getCombat() {
	return combat;
    }

    public IntSupplier getWarLegend() {
	return warLegend;
    }

    public BoolSupplier getWillDie() {
	return willDie;
    }

    public IntSupplier getWood() {
	return wood;
    }

    public void play(Card card) {
	setCard(card);
	card.deploy();
    }

    public void setCard(Card card) {
	if (this.card != null) {
	    this.card.setPosition(null);
	    this.card.setBoardAccessor(new BoardAccessor(null));
	}

	food.setSupplier(apply(card, Card::getFood));
	wood.setSupplier(apply(card, Card::getWood));
	crystal.setSupplier(apply(card, Card::getCrystal));

	buildLevel.setSupplier(apply(card, Card::getBuildLevel));
	age.setSupplier(apply(card, Card::getAge));
	willDie.setSupplier(apply(card, Card::getWillDie));
	dyingAgeToken.setSupplier(apply(card, Card::getDyingAgeToken));

	combat.setSupplier(apply(card, Card::getCombat));
	effectiveCombat.setSupplier(apply(card, Slot::effectiveCombat));

	deployLegend.setSupplier(apply(card, Card::getDeployLegend));
	deployGoldGain.setSupplier(apply(card, Card::getDeployGoldGain));

	warLegend.setSupplier(apply(card, Card::getWarLegend));
	warGoldGain.setSupplier(apply(card, Card::getWarGoldGain));

	payLegend.setSupplier(apply(card, Card::getPayLegend));
	payGoldGain.setSupplier(apply(card, Card::getPayGoldGain));

	ageLegend.setSupplier(apply(card, Card::getAgeLegend));
	ageGoldGain.setSupplier(apply(card, Card::getAgeGoldGain));

	this.card = card;
	if (this.card != null) {
	    this.card.setPosition(cardPosition);
	    this.card.setBoardAccessor(new BoardAccessor(this));
	}
    }

}
