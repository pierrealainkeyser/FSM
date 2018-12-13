package fr.keyser.pt;

import java.util.function.Predicate;
import java.util.stream.Stream;

import fr.keyser.pt.SpecialEffectScope.When;

public class DeployedCard implements InstalledCardBuildPlanner {

    public static final SpecialEffectScope AGING = new SpecialEffectScope(0, When.AGING);

    public static final SpecialEffectScope DEPLOY_LAST = new SpecialEffectScope(3, When.DEPLOYEMENT);

    public static final SpecialEffectScope DEPLOY = new SpecialEffectScope(2, When.DEPLOYEMENT);

    public static final SpecialEffectScope INITIAL_DEPLOY_LAST = new SpecialEffectScope(2, When.INITIAL_DEPLOYEMENT);

    public static final SpecialEffectScope INITIAL_DEPLOY = new SpecialEffectScope(1, When.INITIAL_DEPLOYEMENT);

    public static final SpecialEffectScope INITIAL_DEPLOY_FIRST = new SpecialEffectScope(0, When.INITIAL_DEPLOYEMENT);

    public static Predicate<DeployedCard> meta(MetaCard meta) {
	return p -> p.getMeta().getId() == meta.getId();
    }

    public static Predicate<DeployedCard> hasAgeToken(int i) {
	return p -> p.getAgeToken() == i;
    }

    public static Predicate<DeployedCard> hasALeastAgeToken(int i) {
	return p -> p.getAgeToken() >= i;
    }

    private final MetaCard meta;

    private final Card card;

    private CardCounters counters;

    private CardCounters previous;

    private final CardModel model;

    private final PlayerBoard player;

    private final CardPosition position;

    DeployedCard(PlayerBoard player, CardPosition position, MetaCard meta, CardModel model) {
	this.player = player;
	this.position = position;
	this.meta = meta;
	this.card = meta.getCard();
	this.model = model;

	previous = new CardCounters();
	counters = new CardCounters();
    }

    public DeployedCardInfo getInfo() {
	return new DeployedCardInfo(position, model);
    }

    DeployedCard withMeta(MetaCard meta) {
	return new DeployedCard(player, position, meta, model);
    }

    public DeployedCard find(CardPosition position) {
	return player.find(position).getCard().get();
    }

    @Override
    public String toString() {
	return card.getName();
    }

    void computeDeployGain() {
	counters.getDeploy().apply(card.getDeploy(), this);
    }

    void computeAgeGain() {
	counters.getAge().apply(card.getAge(), this);
    }

    void computeGoldGain() {
	counters.getGold().apply(card.getGold(), this);
    }

    void computeResources() {
	counters.setFood(card.getFood().getValue(this));
	counters.setWood(card.getWood().getValue(this));
	counters.setCrystal(card.getCrystal().getValue(this));
    }

    void computeCombat() {
	counters.setCombat(card.getCombat().getValue(this));
	counters.setMayCombat(card.getMayCombat().getValue(this));
    }

    void computeWarGain() {
	counters.getWar().apply(card.getWar(), this);
    }

    public void doAge() {
	doAge(1);
    }

    public void doAge(int amount) {
	model.setAgeToken(model.getAgeToken() + amount);
    }

    public Stream<ScopedSpecialEffect> effects(When when) {
	Stream<ScopedSpecialEffect> effects = card.getEffects().stream();
	return effects.filter(sse -> sse.match(when));
    }

    public ScopedSpecialEffect effects(int index) {
	return card.getEffects().get(index);
    }

    public int getAgeToken() {
	return model.getAgeToken();
    }

    @Override
    public Card getCard() {
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

    @Override
    public BuildingLevel getLevel() {
	return model.getLevel();
    }

    public PlayerBoard getPlayer() {
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

    void resetCounters() {
	previous = new CardCounters(counters);
	counters = new CardCounters();
    }

    public void setLevel(BuildingLevel level) {
	model.setLevel(level);
	player.buildingHasChanged(this);
    }

    public boolean willDie() {
	if (card instanceof Unit)
	    return ((Unit) card).getDeathCondition().getValue(this);
	else
	    return false;
    }

    public MetaCard getMeta() {
	return meta;
    }

    public void recomputeValues() {
	player.computeValues();
    }

    public boolean isNotProtection() {
	return !model.isProtection();
    }

    public void setProtection(boolean protection) {
	model.setProtection(protection);
    }
}
