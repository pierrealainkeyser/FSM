package fr.keyser.pt;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import fr.keyser.pt.SpecialEffectScope.When;
import fr.keyser.pt.effects.NotifyShiftShapeEffect;

public class DeployedCard implements InstalledCardBuildPlanner {

    public static final SpecialEffectScope AGING = new SpecialEffectScope(0, When.AGING);

    public static final SpecialEffectScope DEPLOY = new SpecialEffectScope(2, When.DEPLOYEMENT);

    public static final SpecialEffectScope PLAY = new SpecialEffectScope(0, When.ON_PLAY);

    public static final SpecialEffectScope INITIAL_DEPLOY_LAST = new SpecialEffectScope(2, When.INITIAL_DEPLOYEMENT);

    public static final SpecialEffectScope INITIAL_DEPLOY = new SpecialEffectScope(1, When.INITIAL_DEPLOYEMENT);

    public static final SpecialEffectScope INITIAL_DEPLOY_FIRST = new SpecialEffectScope(0, When.INITIAL_DEPLOYEMENT);

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

    public DeployedCardInfo getInfo() {
	return new DeployedCardInfo(position, model);
    }

    void shapeShifted() {
	model.setShapeShifted(true);
    }

    DeployedCard withMeta(MetaCard meta) {
	return new DeployedCard(player, position, meta, model);
    }

    public DeployedCard find(CardPosition position) {
	return player.find(position).getCard().get();
    }

    Map<String, CardPosition> getSelected() {
	return model.getSelected();
    }

    void addPositionFor(CardPosition position, String name) {
	model.addPositionFor(position, name);
    }

    @Override
    public String toString() {
	return card.getName();
    }

    void computeDeployGain() {
	counters.setDeployLegend(card.getDeployLegend().getValue(this));
	counters.setDeployGold(card.getDeployGold().getValue(this));

	// clear all selected
	model.setSelected(null);
	model.setShapeShifted(false);
    }

    void computeDyingGain() {
	counters.setDieLegend(card.getDieLegend().getValue(this));
	counters.setDieGold(card.getDieGold().getValue(this));
    }

    void computeGoldGain() {
	counters.setGoldGain(card.getGold().getValue(this));
    }

    void computeValues() {
	counters.setFood(card.getFood().getValue(this));
	counters.setWood(card.getWood().getValue(this));
	counters.setCrystal(card.getCrystal().getValue(this));
	counters.setCombat(card.getCombat().getValue(this));
	counters.setMayCombat(card.getMayCombat().getValue(this));
    }

    void computeWarGain() {
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

    public Stream<ScopedSpecialEffect> intialDeployementEffect() {
	return streamEffect(s -> When.INITIAL_DEPLOYEMENT.match(s.getScope()));
    }

    private Stream<ScopedSpecialEffect> streamEffect(Predicate<ScopedSpecialEffect> predicate) {
	Stream<ScopedSpecialEffect> effects = card.getEffects().stream();
	if (model.isShapeShifted()) {
	    effects = Stream.concat(effects, Stream.of(new ScopedSpecialEffect(INITIAL_DEPLOY_FIRST, NotifyShiftShapeEffect.INSTANCE)));
	}

	return effects.filter(predicate);
    }

    public Stream<ScopedSpecialEffect> effects(When when) {
	return streamEffect(s -> when.match(s.getScope(), this));
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
}
