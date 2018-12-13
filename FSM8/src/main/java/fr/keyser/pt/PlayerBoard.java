package fr.keyser.pt;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.pt.BuildingConstruction.BuildType;
import fr.keyser.pt.CardPosition.Position;
import fr.keyser.pt.SpecialEffectScope.When;
import fr.keyser.pt.event.CardBuildingLevelChanged;
import fr.keyser.pt.event.CardDeploymentChanged;
import fr.keyser.pt.event.CardRefreshInfo;
import fr.keyser.pt.event.PlayerBuildPlanEvent;
import fr.keyser.pt.event.PlayerDoDeployEvent;
import fr.keyser.pt.event.PlayerDoDraftEvent;
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
 * <li>{@link PlayerBoard#computeAgeGain()}</li>
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

    private PlayerCounters previous = new PlayerCounters();

    private List<DeployedCard> dying = new ArrayList<>();

    private List<CardSlot> front = ctx(Position.FRONT, 3);

    private final PlayerBoardModel model;

    private final UUID uuid;

    public PlayerBoard(UUID uuid, PlayerBoardModel model, Board board) {
	this.uuid = uuid;
	this.model = model;
	this.board = board;
    }

    void doRefresh() {
	all().forEach(dc -> forward(new CardDeploymentChanged(dc, this, true)));
	buildings().forEach(this::buildingHasChanged);
	all().forEach(dc -> forward(new CardRefreshInfo(dc, this)));

	forward(new PlayerGoldChanged(this, getGold()));
	forward(new PlayerLegendChanged(this, getLegend()));

	forward(new PlayerDoDeployEvent(uuid, model.getToDeploy()));
	forward(new PlayerBuildPlanEvent(uuid, model.getBuildPlan()));
	forward(new PlayerDoDraftEvent(uuid, model.getToDraft()));
    }

    @Override
    public void refresh() {
	board.refreshAll();
    }

    private void addGold(int gold) {
	model.addGold(gold);
	if (gold != 0)
	    forward(new PlayerGoldChanged(this, model.getGold()));

    }

    @Override
    public void agePhase() {
	collectDying();
	collectEffects(When.AGING);
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

    void clearBuilding() {
	model.getBuildPlan().clear();
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
	counters.sumWar(counters(all()));
    }

    void computeAgeGain() {
	all().forEach(DeployedCard::computeAgeGain);
	counters.sumAge(counters(all()));
    }

    void computeValues() {
	all().forEach(DeployedCard::computeResources);
	counters.sumResources(counters(all()));

	all().forEach(DeployedCard::computeGoldGain);
	counters.sumGold(counters(all()));

	all().forEach(DeployedCard::computeCombat);
	counters.sumCombat(counters(all()));
    }

    void computeWarGain() {
	all().forEach(DeployedCard::computeWarGain);
	counters.sumWar(counters(all()));

	counters.getWar().addLegend(POINT_PER_VICTORY * getVictoriousWar());

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
	collectEffects(all().filter(DeployedCard::isInitialDeploy), When.INITIAL_DEPLOYEMENT);
	collectEffects(When.DEPLOYEMENT);
    }

    void doAge() {
	units().forEach(DeployedCard::doAge);
    }

    public Stream<DeployedCard> dyings() {
	return dying.stream();
    }

    @Override
    public void endAgePhase() {
	computeAgeGain();
	fireEffects();
	removeDead();
    }

    @Override
    public void endBuildPhase() {
	clearBuilding();
    }

    @Override
    public void endOfDeployPhase() {
	fireEffects();
	computeValues();
	computeDeployGain();

	all().forEach(dc -> forward(new CardRefreshInfo(dc, this)));
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

    void collectEffects(Stream<DeployedCard> cards, When when) {
	List<FiredEffect> fired = cards.flatMap(d -> d.effects(when).map(e -> new FiredEffect(d, e)))
	        .collect(toList());
	fired.sort(Comparator.comparing(FiredEffect::getOrder));
	model.setEffects(fired);
    }

    void collectEffects(When when) {
	collectEffects(all(), when);
    }

    void fireEffects() {
	model.getEffects().forEach(f -> f.fire(this));
    }

    private void forward(Object event) {
	board.forward(event);
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
	return counters.getGold().getGold();
    }

    public List<DeployedCardInfo> getInfos() {
	return all().map(DeployedCard::getInfo).collect(Collectors.toList());
    }

    @Override
    public List<FiredEffect> getInputActions() {
	return model.getEffects();
    }

    List<MetaCard> getToDraft() {
	return model.getToDraft();
    }

    @Override
    public UUID getUUID() {
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

    }

    @Override
    public boolean hasInputActions() {
	return !model.getEffects().isEmpty();
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
	Optional<FiredEffect> first = model.getEffects().stream().filter(f -> f.match(action)).findFirst();
	first.ifPresent(f -> f.use(action));
    }

    @Override
    public void processDeployCardAction(DoDeployCard action) {
	Optional<MetaCard> first = model.getToDeploy().stream().filter(MetaCard.sameId(action.getSource())).findFirst();
	if (first.isPresent()) {
	    MetaCard meta = first.get();

	    CardSlot slot = find(action.getTarget());

	    DeployedCard previous = slot.getCard().orElse(null);

	    if (previous != null)
		board.moveToDiscard(previous.getMeta());

	    DeployedCard dc = slot.deploy(meta, board.getTurnValue());

	    if (previous != null)
		forward(new CardDeploymentChanged(previous, this, false));

	    forward(new CardDeploymentChanged(dc, this, true));

	    model.getToDeploy().remove(meta);

	    int goldDelta = ((Unit) meta.getCard()).getGoldCost();
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

	forward(new PlayerDoDeployEvent(uuid, toDeploy));
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

    void acquireLastDraft() {
	List<MetaCard> toDraft = model.getToDraft();
	List<MetaCard> toDeploy = model.getToDeploy();

	toDeploy.addAll(toDraft);
	toDraft.clear();

	forward(new PlayerDoDeployEvent(uuid, toDeploy));
    }

    void resetCounters() {
	previous = new PlayerCounters(counters);

	counters = new PlayerCounters();
	all().forEach(DeployedCard::resetCounters);
    }

    boolean sameTurn(CardModel model) {
	return board.sameTurn(model);
    }

    void setToDraft(List<MetaCard> toDraft) {
	model.setToDraft(toDraft);

	forward(new PlayerDoDraftEvent(uuid, toDraft));
    }

    void setVictoriousWar(int victoriousWar) {
	counters.setVictoriousWar(victoriousWar);
    }

    public Stream<DeployedCard> units() {
	return asDeployedCard(Stream.concat(front.stream(), back.stream()));
    }

    public Optional<DeployedCard> cardAt(CardPosition position) {
	return find(position).getCard();
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
