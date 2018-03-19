package fr.keyser.pt;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static final int POINT_PER_VICTORY = 3;

    private final static Stream<DeployedCard> asDeployedCard(Stream<CardSlot> input) {
	return input.map(CardSlot::getCard).filter(Optional::isPresent).map(Optional::get);
    }

    private static Stream<CardCounters> counters(Stream<DeployedCard> cards) {
	return cards.map(DeployedCard::getCounters);
    }

    private List<CardSlot> back = ctx(Position.BACK, 2);

    private List<CardSlot> building = ctx(Position.BUILDING, 4);

    private PlayerCounters counters;

    private List<DeployedCard> dying = new ArrayList<>();

    private List<CardSlot> front = ctx(Position.FRONT, 3);

    private final PlayerBoardModel model;

    private final Board board;

    private final UUID uuid;

    public PlayerBoard(UUID uuid, PlayerBoardModel model, Board board) {
	this.uuid = uuid;
	this.model = model;
	this.board = board;
    }

    @Override
    public void visit(PlayerBoardVisitor visitor) {
	visitor.player(uuid, model.getGold(), model.getLegend());

	if (counters != null)
	    visitor.victoriousWar(uuid, counters.getVictoriousWar());

	units().forEach(d -> {
	    Unit u = (Unit) d.getCard();
	    visitor.unit(uuid, d.getPosition(), u, d.isInitialDeploy(), d.getAgeToken(), d.getCombat(), d.isMayCombat());
	});

	buildings().forEach(d -> {
	    Building b = (Building) d.getCard();
	    visitor.building(uuid, d.getPosition(), b, d.getLevel(), d.getCombat(), d.isMayCombat());
	});

	visitor.toDraft(uuid, unmodifiableList(model.getToDraft()));
	visitor.toBuild(uuid, unmodifiableList(model.getBuildPlan()));
	visitor.toDeploy(uuid, unmodifiableList(model.getToDeploy()));
    }

    List<MetaCard> getToDraft() {
	return model.getToDraft();
    }

    void setToDraft(List<MetaCard> toDraft) {
	model.setToDraft(toDraft);
    }

    void addInputAction(DeployedCard ctx, CardPositionSelector input) {
	model.getInputActions().put(ctx.getPosition(), input);
    }

    Stream<DeployedCard> all() {
	return Stream.concat(units(), buildings());
    }

    boolean sameTurn(CardModel model) {
	return board.sameTurn(model);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.keyser.pt.PlayerBoardContract#processDraft(int)
     */
    @Override
    public void processDraft(int id) {
	List<MetaCard> toDeploy = model.getToDeploy();
	removeDrafted(id, toDeploy::add);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.keyser.pt.PlayerBoardContract#processDiscard(int)
     */
    @Override
    public void processDiscard(int id) {
	removeDrafted(id, board::moveToDiscard);
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

    /*
     * (non-Javadoc)
     * 
     * @see fr.keyser.pt.PlayerBoardContract#processDeployCardAction(fr.keyser.pt.
     * DoDeployCard)
     */
    @Override
    public void processDeployCardAction(DoDeployCard action) {
	Optional<MetaCard> first = model.getToDeploy().stream().filter(MetaCard.sameId(action.getSource())).findFirst();
	if (first.isPresent()) {
	    MetaCard meta = first.get();

	    CardSlot slot = find(action.getTarget());

	    slot.getCard().ifPresent(m -> board.moveToDiscard(m.getMeta()));

	    slot.deploy(meta, board.getTurnValue());

	    model.getToDeploy().remove(meta);
	    model.addGold(-((Unit) meta.getCard()).getGoldCost());
	}
    }

    void clearBuilding() {
	model.getBuildPlan().clear();
    }

    @Override
    public void doBuild(int index) {
	BuildingConstruction plan = model.getBuildPlan().get(index);
	MetaCard meta = plan.getBuilding();

	if (BuildType.UPGRADE == plan.getType()) {
	    CardSlot slot = building.stream().filter(c -> c.isCard(meta)).findFirst().get();
	    slot.upgrade();

	} else {
	    CardSlot slot = building.stream().filter(CardSlot::isEmpty).findFirst().get();
	    slot.build(meta, plan.getLevel());
	}
	model.addGold(plan.getGoldCost());
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.keyser.pt.PlayerBoardContract#keepToDeploy(int)
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.keyser.pt.PlayerBoardContract#processCardAction(fr.keyser.pt.CardAction)
     */
    @Override
    public void processCardAction(CardAction action) {
	CardPositionSelector selector = model.getInputActions().remove(action.getSource());
	selector.process(this, action.getTarget());
    }

    Stream<DeployedCard> buildings() {
	return asDeployedCard(building.stream());
    }

    void clearInputActions() {
	model.getInputActions().clear();
    }

    private static class FiredEffect {
	public FiredEffect(DeployedCard card, ScopedSpecialEffect effect) {
	    this.card = card;
	    this.effect = effect;
	}

	private final DeployedCard card;

	private final ScopedSpecialEffect effect;

	public int getOrder() {
	    return effect.getScope().getOrder();
	}

	public void fire() {
	    effect.getSpecialEffect().apply(card);
	}
    }

    void fireEffect(When when) {

	List<FiredEffect> fired = units().flatMap(d -> d.firedEffects(when).map(e -> new FiredEffect(d, e))).collect(Collectors.toList());
	fired.sort(Comparator.comparing(FiredEffect::getOrder));
	fired.forEach(FiredEffect::fire);

    }

    void collectBuilding(List<MetaCard> bluePrints) {
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

	model.addGold(counters.getDeployGold());
	model.addLegend(counters.getDeployLegend());
    }

    void computeDyingGain() {
	all().forEach(DeployedCard::computeDyingGain);
	counters(all()).forEach(counters::sumDyingGain);

	model.addGold(counters.getDieGold());
	model.addLegend(counters.getDieLegend());
    }

    void computeValues() {
	counters.setGoldGain(2);

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

	model.addGold(counters.getWarGold());
	model.addLegend(counters.getWarLegend());
    }

    private List<CardSlot> ctx(Position position, int number) {
	CardSlot[] ctx = new CardSlot[number];
	for (int i = 0; i < ctx.length; ++i)
	    ctx[i] = new CardSlot(this, new CardPosition(position, i));
	return Arrays.asList(ctx);
    }

    void doAge() {
	units().forEach(DeployedCard::doAge);
    }

    Stream<DeployedCard> dyings() {
	return dying.stream();
    }

    public CardSlot find(CardPosition position) {
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

    void gainGold() {
	model.addGold(counters.getGoldGain());
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

    int getGoldGain() {
	return counters.getGoldGain();
    }

    int getVictoriousWar() {
	return counters.getVictoriousWar();
    }

    int getWood() {
	return counters.getFood();
    }

    public void preserveFromDeath(CardPosition position) {
	Iterator<DeployedCard> it = dying.iterator();
	while (it.hasNext()) {
	    DeployedCard next = it.next();
	    if (next.getPosition().equals(position))
		it.remove();
	}

    }

    void removeDead() {
	dyings().map(DeployedCard::getPosition).map(this::find).forEach(CardSlot::clear);
	dying.clear();
    }

    void resetCounters() {
	counters = new PlayerCounters();
	all().forEach(DeployedCard::resetCounters);
    }

    void setVictoriousWar(int victoriousWar) {
	counters.setVictoriousWar(victoriousWar);
    }

    public Stream<DeployedCard> units() {
	return asDeployedCard(Stream.concat(front.stream(), back.stream()));
    }

    @Override
    public UUID getUuid() {
	return uuid;
    }
}
