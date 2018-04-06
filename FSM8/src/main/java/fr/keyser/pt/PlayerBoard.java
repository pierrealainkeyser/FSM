package fr.keyser.pt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.pt.BuildingConstruction.BuildType;
import fr.keyser.pt.CardPosition.Position;
import fr.keyser.pt.SpecialEffectScope.When;
import fr.keyser.pt.event.CardAgeChanged;
import fr.keyser.pt.event.CardBuildingLevelChanged;
import fr.keyser.pt.event.CardDeploymentChanged;
import fr.keyser.pt.event.PlayerGoldChanged;
import fr.keyser.pt.event.PlayerLegendChanged;

/**
 * 
 * <ul>
 * <li>Phase 2 - deployement
 * <ul>
 * <li>{@link PlayerBoard#computeValues()}</li>
 * <li>Phase 2 Effect (Drop, Constant)
 * <li>{@link PlayerBoard#computeValues()}</li>
 * </ul>
 * </li>
 * <li>Phase 3 - combat
 * <ul>
 * <li>int combat={@link PlayerBoard#getCombat()}</li>
 * <li>int victoriousWar=...;
 * <li>{@link PlayerBoard#setVictoriousWar(int)}</li>
 * <li>{@link PlayerBoard#computeWarGain()}</li>
 * </ul>
 * </li>
 * <li>Phase 4 - revenu
 * <ul>
 * <li>{@link PlayerBoard#gainGold()}</li>
 * </ul>
 * </li>
 * <li>Phase 6 - aging
 * <ul>
 * <li>{@link PlayerBoard#collectDying()}</li>
 * <li>Phase 6 Effect
 * <li>{@link PlayerBoard#computeDyingGain()}</li>
 * <li>{@link PlayerBoard#removeDead()}</li>
 * <li>{@link PlayerBoard#doAge()}</li>
 * </ul>
 * </li>
 * 
 * </ul>
 * 
 * @author pakeyser
 *
 */
public final class PlayerBoard implements PlayerBoardContract {

    public final static class FiredEffect {
	private final DeployedCard card;

	private final ScopedSpecialEffect effect;

	private FiredEffect(DeployedCard card, ScopedSpecialEffect effect) {
	    this.card = card;
	    this.effect = effect;
	}

	private void fire() {
	    effect.getSpecialEffect().apply(card);
	}

	public DeployedCard getCard() {
	    return card;
	}

	public String getName() {
	    return effect.getName();
	}

	private int getOrder() {
	    return effect.getScope().getOrder();
	}
    }

    private static final int POINT_PER_VICTORY = 3;

    private final static Stream<DeployedCard> asDeployedCard(Stream<CardSlot> input) {
	return input.map(CardSlot::getCard).filter(Optional::isPresent).map(Optional::get);
    }

    private static Stream<CardCounters> counters(Stream<DeployedCard> cards) {
	return cards.map(DeployedCard::getCounters);
    }

    private List<CardSlot> back = ctx(Position.BACK, 2);

    private final Board board;

    private List<CardSlot> building = ctx(Position.BUILDING, 4);

    private PlayerCounters counters = new PlayerCounters();

    private List<DeployedCard> dying = new ArrayList<>();

    private List<CardSlot> front = ctx(Position.FRONT, 3);

    private final PlayerBoardModel model;

    private final UUID uuid;

    public PlayerBoard(UUID uuid, PlayerBoardModel model, Board board) {
	this.uuid = uuid;
	this.model = model;
	this.board = board;
    }

    private void addGold(int gold) {
	model.addGold(gold);
	if (gold != 0)
	    forward(new PlayerGoldChanged(this, model.getGold()));

    }

    private void addLegend(int legend) {
	model.addLegend(legend);
	if (legend != 0)
	    forward(new PlayerLegendChanged(this, model.getLegend()));
    }

    @Override
    public void agePhase() {
	clearInputActions();
	collectDying();
	registerAsyncEffect(all(), When.AGING);
    }

    Stream<DeployedCard> all() {
	return Stream.concat(units(), buildings());
    }

    void buildingHasChanged(DeployedCard deployedCard) {
	forward(new CardBuildingLevelChanged(deployedCard, this, deployedCard.getLevel()));
    }

    public Stream<DeployedCard> buildings() {
	return asDeployedCard(building.stream());
    }

    @Override
    public void buildPhase() {
	collectBuilding(board.getBuildings());

    }

    void cardHasAged(DeployedCard deployedCard) {
	forward(new CardAgeChanged(deployedCard, this, deployedCard.getAgeToken()));
    }

    void clearBuilding() {
	model.getBuildPlan().clear();
    }

    private void clearInputActions() {
	model.getInputActions().clear();
    }

    void collectBuilding(Collection<MetaCard> bluePrints) {
	List<BuildingConstruction> buildPlan = new BuildingPlanner(model, counters, all()).compute(bluePrints);
	model.setBuildPlan(buildPlan);
    }

    void collectDying() {
	dying.clear();
	units().filter(DeployedCard::willDie).forEach(dying::add);
    }

    void computeDeployGain() {
	all().forEach(DeployedCard::computeDeployGain);
	counters(all()).forEach(counters::sumDeployGain);

	addGold(counters.getDeployGold());
	addLegend(counters.getDeployLegend());
    }

    void computeDyingGain() {
	all().forEach(DeployedCard::computeDyingGain);
	counters(all()).forEach(counters::sumDyingGain);

	addGold(counters.getDieGold());
	addLegend(counters.getDieLegend());
    }

    void computeValues() {
	counters.resetBasicCounters();

	all().forEach(DeployedCard::computeGoldGain);
	counters(all()).forEach(counters::sumGold);

	all().forEach(DeployedCard::computeValues);
	counters(all()).forEach(counters::sumValues);
    }

    void computeWarGain() {
	counters.setWarLegend(POINT_PER_VICTORY * getVictoriousWar());

	units().forEach(DeployedCard::computeWarGain);
	buildings().forEach(DeployedCard::computeWarGain);

	counters(all()).forEach(counters::sumWarGain);

	addGold(counters.getWarGold());
	addLegend(counters.getWarLegend());
    }

    private List<CardSlot> ctx(Position position, int number) {
	CardSlot[] ctx = new CardSlot[number];
	for (int i = 0; i < ctx.length; ++i)
	    ctx[i] = new CardSlot(this, position.index(i));
	return Arrays.asList(ctx);
    }

    @Override
    public void deployPhase() {
	computeValues();
	clearInputActions();
	registerAsyncEffect(all(), When.DEPLOYEMENT);
    }

    void doAge() {
	units().forEach(DeployedCard::doAge);
    }

    public Stream<DeployedCard> dyings() {
	return dying.stream();
    }

    @Override
    public void endAgePhase() {
	computeDyingGain();
	fireEffect(When.AGING);
	removeDead();
    }

    @Override
    public void endBuildPhase() {
	clearBuilding();
    }

    @Override
    public void endOfDeployPhase() {
	fireEffect(When.DEPLOYEMENT);
	computeValues();
	computeDeployGain();
    }

    CardSlot find(CardPosition position) {
	List<CardSlot> list = null;
	switch (position.getPosition()) {
	case BACK:
	    list = back;
	    break;
	case BUILDING:
	    list = building;
	    break;
	case FRONT:
	    list = front;
	    break;
	default:
	    throw new IllegalArgumentException("Invalid position");
	}
	return list.get(position.getIndex());
    }

    void fireEffect(Stream<DeployedCard> cards, When when) {
	List<FiredEffect> fired = cards.flatMap(d -> d.effects(when).map(e -> new FiredEffect(d, e)))
	        .collect(Collectors.toList());
	fired.sort(Comparator.comparing(FiredEffect::getOrder));
	for (FiredEffect f : fired) {
	    forward(f);
	    f.fire();
	}
    }

    void fireEffect(When when) {
	fireEffect(all(), when);
    }

    private void forward(Object event) {
	board.forward(event);
    }

    void gainGold() {
	addGold(counters.getGoldGain());
    }

    public Board getBoard() {
	return board;
    }

    int getCombat() {
	return counters.getCombat();
    }

    int getCrystal() {
	return counters.getCrystal();
    }

    int getFood() {
	return counters.getFood();
    }

    public int getGold() {
	return model.getGold();
    }

    int getGoldGain() {
	return counters.getGoldGain();
    }

    public List<DeployedCardInfo> getInfos() {
	return all().map(DeployedCard::getInfo).collect(Collectors.toList());
    }

    public Map<CardPosition, List<TargetedEffectDescription>> getInputActions() {
	return model.getInputActions();
    }

    List<MetaCard> getToDraft() {
	return model.getToDraft();
    }

    @Override
    public UUID getUuid() {
	return uuid;
    }

    int getVictoriousWar() {
	return counters.getVictoriousWar();
    }

    int getWood() {
	return counters.getWood();
    }

    @Override
    public void goldPhase() {
	gainGold();

    }

    @Override
    public boolean hasInputActions() {
	return !model.getInputActions().isEmpty();
    }

    @Override
    public void keepToDeploy(int id) {
	Iterator<MetaCard> it = model.getToDeploy().iterator();
	while (it.hasNext()) {
	    MetaCard next = it.next();
	    if (next.getId() != id) {
		it.remove();
		board.moveToDiscard(next);
	    }
	}
    }

    public void preserveFromDeath(CardPosition position) {
	Iterator<DeployedCard> it = dying.iterator();
	while (it.hasNext()) {
	    DeployedCard next = it.next();
	    if (next.getPosition().equals(position))
		it.remove();
	}

    }

    @Override
    public void processBuild(int index) {
	BuildingConstruction plan = model.getBuildPlan().get(index);
	MetaCard meta = plan.getBuilding();

	if (BuildType.UPGRADE == plan.getType()) {
	    CardSlot slot = building.stream().filter(c -> c.isCard(meta)).findFirst().get();
	    slot.upgrade();

	} else {
	    CardSlot slot = building.stream().filter(CardSlot::isEmpty).findFirst().get();
	    BuildingLevel level = plan.getLevel();
	    slot.build(meta, level);
	    forward(new CardBuildingLevelChanged(slot.getCard().get(), this, level));
	}
	addGold(-plan.getGoldCost());
    }

    @Override
    public void processCardAction(CardAction action) {

	CardPosition source = action.getSource();
	model.getInputActions().remove(source);

	DeployedCard from = find(source).getCard().get();
	for (Entry<String, CardPosition> e : action.getTarget().entrySet()) {
	    from.addPositionFor(e.getValue(), e.getKey());
	}

    }

    @Override
    public void processDeployCardAction(DoDeployCard action) {
	Optional<MetaCard> first = model.getToDeploy().stream().filter(MetaCard.sameId(action.getSource())).findFirst();
	if (first.isPresent()) {
	    MetaCard meta = first.get();

	    CardSlot slot = find(action.getTarget());

	    slot.getCard().ifPresent(m -> {
		board.moveToDiscard(m.getMeta());
		forward(new CardDeploymentChanged(m, this, false));
	    });

	    DeployedCard dc = slot.deploy(meta, board.getTurnValue());
	    forward(new CardDeploymentChanged(dc, this, true));

	    model.getToDeploy().remove(meta);

	    int goldDelta = ((Unit) meta.getCard()).getGoldCost();
	    counters.setDeployGold(counters.getDeployGold() - goldDelta);

	    fireEffect(Stream.of(dc), When.ON_PLAY);
	}
    }

    @Override
    public void processDiscard(int id) {
	removeDrafted(id, board::moveToDiscard);
    }

    @Override
    public void processDraft(int id) {
	List<MetaCard> toDeploy = model.getToDeploy();
	removeDrafted(id, toDeploy::add);
    }

    public void redeploy(MetaCard unit, CardPosition position) {
	CardSlot slot = find(position);

	DeployedCard dc = slot.redeploy(unit);
	dc.shapeShifted();
	forward(new CardDeploymentChanged(dc, this, true));
    }

    void registerAsyncEffect(Stream<DeployedCard> cards, When when) {
	cards.forEach(d -> d.effects(when).filter(ScopedSpecialEffect::isAsync).forEach(e -> {
	    List<TargetedEffectDescription> asyncEffect = e.asyncEffect(d);
	    if (asyncEffect != null)
		model.getInputActions().put(d.getPosition(), asyncEffect);
	}));

    }

    void removeDead() {
	dyings().map(DeployedCard::getPosition).map(this::find).forEach(CardSlot::clear);
	dying.clear();
    }

    private void removeDrafted(int id, Consumer<MetaCard> consumer) {
	List<MetaCard> toDraft = model.getToDraft();
	Optional<MetaCard> first = toDraft.stream().filter(MetaCard.sameId(id)).findFirst();
	if (first.isPresent()) {
	    MetaCard meta = first.get();
	    toDraft.remove(meta);
	    consumer.accept(meta);
	}
    }

    void resetCounters() {
	counters = new PlayerCounters();
	all().forEach(DeployedCard::resetCounters);
    }

    boolean sameTurn(CardModel model) {
	return board.sameTurn(model);
    }

    void setToDraft(List<MetaCard> toDraft) {
	model.setToDraft(toDraft);
    }

    void setVictoriousWar(int victoriousWar) {
	counters.setVictoriousWar(victoriousWar);
    }

    public Stream<DeployedCard> units() {
	return asDeployedCard(Stream.concat(front.stream(), back.stream()));
    }

    /**
     * Test only
     * 
     * @param position
     * @param cardModel
     */
    void useCard(CardPosition position, CardModel cardModel) {
	find(position).withModel(cardModel);
    }

    public int getLegend() {
	return model.getLegend();
    }
}
