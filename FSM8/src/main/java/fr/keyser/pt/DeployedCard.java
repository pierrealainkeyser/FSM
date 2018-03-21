package fr.keyser.pt;

import java.util.function.Predicate;
import java.util.stream.Stream;

import fr.keyser.pt.SpecialEffectScope.When;

public class DeployedCard {

    public static final SpecialEffectScope AGING_ASYNCHRONOUS = new SpecialEffectScope(2, When.AGING);

    public static final SpecialEffectScope DEPLOY_ASYNCHRONOUS = new SpecialEffectScope(2, When.DEPLOYEMENT);

    public static final SpecialEffectScope INITIAL_DEPLOY_ASYNCHRONOUS = new SpecialEffectScope(2, When.INITIAL_DEPLOYEMENT);

    public static final SpecialEffectScope INITIAL_DEPLOY_SYNCHRONOUS_LAST = new SpecialEffectScope(1, When.INITIAL_DEPLOYEMENT);

    public static final SpecialEffectScope INITIAL_DEPLOY_SYNCHRONOUS = new SpecialEffectScope(0, When.INITIAL_DEPLOYEMENT);

    public static Predicate<DeployedCard> hasAgeToken(int i) {
	return p -> p.getAgeToken() == i;
    }

    public static Predicate<DeployedCard> hasALeastAgeToken(int i) {
	return p -> p.getAgeToken() >= i;
    }

    private final MetaCard meta;

    private final Card card;

    private CardCounters counters;

    private final CardModel model;

    private final PlayerBoard player;

    private final CardPosition position;

    DeployedCard(PlayerBoard player, CardPosition position, MetaCard meta, CardModel model) {
	this.player = player;
	this.position = position;
	this.meta = meta;
	this.card = meta.getCard();
	this.model = model;

	resetCounters();
    }

    @Override
    public String toString() {
	return card.getName();
    }

    public void addInputAction(CardPositionSelector input) {
	getPlayer().addInputAction(this, input);

    }

    public Stream<DeployedCard> buildings() {
	return getPlayer().buildings();
    }

    public void computeDeployGain() {
	counters.setDeployLegend(card.getDeployLegend().getValue(this));
	counters.setDeployGold(card.getDeployGold().getValue(this));
    }

    public void computeDyingGain() {
	counters.setDieLegend(card.getDieLegend().getValue(this));
	counters.setDieGold(card.getDieGold().getValue(this));
    }

    public void computeGoldGain() {
	counters.setGoldGain(card.getGold().getValue(this));
    }

    public void computeValues() {
	counters.setFood(card.getFood().getValue(this));
	counters.setWood(card.getWood().getValue(this));
	counters.setCrystal(card.getCrystal().getValue(this));
	counters.setCombat(card.getCombat().getValue(this));
	counters.setMayCombat(card.getMayCombat().getValue(this));
    }

    public void computeWarGain() {
	counters.setWarLegend(card.getWarLegend().getValue(this));
	counters.setWarGold(card.getWarGold().getValue(this));
    }

    public void doAge() {
	doAge(1);
    }

    public void doAge(int amount) {
	model.setAgeToken(model.getAgeToken() + amount);
	player.cardHasAged(this);
    }

    public Stream<DeployedCard> dyings() {
	return getPlayer().dyings();
    }

    public Stream<ScopedSpecialEffect> firedEffects(When when) {
	if (card instanceof Unit) {
	    return ((Unit) card).getEffects().stream().filter(when(when));
	} else
	    return Stream.empty();
    }

    public int getAgeToken() {
	return model.getAgeToken();
    }

    Card getCard() {
	return card;
    }

    public int getCombat() {
	return counters.getCombat();
    }

    public boolean isMayCombat() {
	return counters.isMayCombat();
    }

    CardCounters getCounters() {
	return counters;
    }

    public BuildingLevel getLevel() {
	return model.getLevel();
    }

    PlayerBoard getPlayer() {
	return player;
    }

    public CardPosition getPosition() {
	return position;
    }

    public boolean isInitialDeploy() {
	return getPlayer().sameTurn(model);
    }

    public boolean isLevel1() {
	return BuildingLevel.LEVEL1 == getLevel();
    }

    public boolean isLevel2() {
	return BuildingLevel.LEVEL2 == getLevel();
    }

    public boolean isOnFrontLine() {
	return getPosition().isOnFrontLine();
    }

    public void resetCounters() {
	counters = new CardCounters();
    }

    public void setLevel(BuildingLevel level) {
	model.setLevel(level);
	player.buildingHasChanged(this);
    }

    public Stream<DeployedCard> units() {
	return getPlayer().units();
    }

    private Predicate<ScopedSpecialEffect> when(When when) {
	return s -> when.match(s.getScope(), this);
    }

    public boolean willDie() {
	return ((Unit) card).getDeathCondition().getValue(this);
    }

    public MetaCard getMeta() {
	return meta;
    }
}
