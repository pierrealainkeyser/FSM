package fr.keyser.pt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.pt.CardPosition.Position;
import fr.keyser.pt.SpecialEffectScope.When;
import fr.keyser.pt.units.GrandArchitect;
import fr.keyser.pt.units.Notable;
import fr.keyser.pt.units.WoodNegociant;

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
public final class PlayerBoard {

    private class Builder {
	private RawBuildingCost available;

	private int baseCost;

	private boolean hasGrandArchitect;

	private boolean hasNotable;

	private boolean hasWoodNegociant;

	public Builder() {
	    available = new RawBuildingCost()
	            .crystal(counters.getCrystal())
	            .food(counters.getFood())
	            .wood(counters.getFood())
	            .gold(model.getGold());

	    baseCost = 2 * (int) buildings().count();

	    units().forEach(c -> {
		Card u = c.getCard();
		if (u instanceof Notable)
		    hasNotable = true;
		else if (u instanceof GrandArchitect)
		    hasGrandArchitect = true;
		else if (u instanceof WoodNegociant)
		    hasWoodNegociant = true;
	    });
	}

	private RawBuildingCost buildCost(RawBuildingCost cost) {
	    if (hasNotable)
		return goldGost(cost);
	    else
		return goldGost(cost.clone().gold(cost.getGold() + baseCost));
	}

	public Optional<RawBuildingCost> buildLevel1(Building building) {
	    List<RawBuildingCost> cost = new ArrayList<>();

	    RawBuildingCost level1 = building.getLevel1();
	    cost.add(buildCost(level1));
	    RawBuildingCost alterned = level1.getAlternate();
	    if (alterned != null)
		cost.add(buildCost(alterned));

	    return cost.stream().filter(Objects::nonNull).min(MIN_COST);
	}

	public Optional<RawBuildingCost> buildLevel2(Building building) {
	    List<RawBuildingCost> cost = new ArrayList<>();

	    RawBuildingCost level1 = building.getLevel1();
	    RawBuildingCost level2 = building.getLevel2();
	    cost.add(buildCost(level2.add(level1)));

	    RawBuildingCost alterned1 = level1.getAlternate();
	    RawBuildingCost alterned2 = level2.getAlternate();

	    if (alterned2 != null)
		cost.add(buildCost(alterned2.add(level1)));

	    if (alterned1 != null) {
		cost.add(buildCost(level2.add(alterned1)));
		if (alterned2 != null) {
		    cost.add(buildCost(alterned2.add(alterned1)));
		}
	    }

	    return cost.stream().filter(Objects::nonNull).min(MIN_COST);
	}

	private RawBuildingCost goldGost(RawBuildingCost cost) {
	    RawBuildingCost lacking = cost.required(available);
	    if (lacking.isCovered())
		return cost;

	    // modification du cout
	    RawBuildingCost altered = cost.clone();
	    if (hasGrandArchitect || hasWoodNegociant) {
		if (hasGrandArchitect) {
		    increaseGoldCost(lacking, RawBuildingCost::getCrystal, altered, RawBuildingCost::crystal);
		    increaseGoldCost(lacking, RawBuildingCost::getFood, altered, RawBuildingCost::food);
		}

		increaseGoldCost(lacking, RawBuildingCost::getWood, altered, RawBuildingCost::wood);

		RawBuildingCost newCost = altered.required(available);
		if (newCost.isCovered())
		    return altered;
	    }

	    return null;
	}

	private void increaseGoldCost(RawBuildingCost lacking, Function<RawBuildingCost, Integer> read, RawBuildingCost target,
	        BiConsumer<RawBuildingCost, Integer> write) {
	    int required = read.apply(lacking);
	    if (required > 0) {
		target.gold(target.getGold() + required);
		write.accept(target, read.apply(target) - required);
	    }

	}

	public Optional<RawBuildingCost> upgrade(Building building) {
	    List<RawBuildingCost> cost = new ArrayList<>();

	    RawBuildingCost level2 = building.getLevel2();
	    cost.add(goldGost(level2));
	    RawBuildingCost alterned = level2.getAlternate();
	    if (alterned != null)
		cost.add(goldGost(alterned));

	    return cost.stream().filter(Objects::nonNull).min(MIN_COST);

	}

    }

    private final static Comparator<RawBuildingCost> MIN_COST = Comparator.comparingInt(RawBuildingCost::getGold);

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

    public PlayerBoard(PlayerBoardModel model, Board board) {
	this.model = model;
	this.board = board;
    }

    void addInputAction(DeployedCard ctx, CardPositionSelector input) {
	model.getInputActions().put(ctx.getPosition(), input);
    }

    public Stream<DeployedCard> all() {
	return Stream.concat(units(), buildings());
    }

    boolean sameTurn(CardModel model) {
	return board.sameTurn(model);
    }

    public void processDraft(int id) {
	List<MetaCard> toDeploy = model.getToDeploy();
	removeDrafted(id, toDeploy::add);
    }

    public void processDiscard(int id) {
	removeDrafted(id, board::moveToDiscard);
    }

    public void removeDrafted(int id, Consumer<MetaCard> consumer) {
	List<MetaCard> toDraft = model.getToDraft();
	Optional<MetaCard> first = toDraft.stream().filter(MetaCard.sameId(id)).findFirst();
	if (first.isPresent()) {
	    MetaCard meta = first.get();
	    toDraft.remove(meta);
	    consumer.accept(meta);
	}
    }

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

    public void processCardAction(CardAction action) {
	CardPositionSelector selector = model.getInputActions().remove(action.getSource());
	selector.process(this, action.getTarget());
    }

    public Stream<DeployedCard> buildings() {
	return asDeployedCard(building.stream());
    }

    public void clearInputActions() {
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

    public void fireEffect(When when) {

	List<FiredEffect> fired = units().flatMap(d -> d.firedEffects(when).map(e -> new FiredEffect(d, e))).collect(Collectors.toList());
	fired.sort(Comparator.comparing(FiredEffect::getOrder));
	fired.forEach(FiredEffect::fire);

    }

    public void collectDying() {
	dying.clear();
	units().filter(DeployedCard::willDie).forEach(dying::add);
    }

    public void computeDeployGain() {
	all().forEach(DeployedCard::computeDeployGain);
	counters(all()).forEach(counters::sumDeployGain);

	model.addGold(counters.getDeployGold());
	model.addLegend(counters.getDeployLegend());
    }

    public void computeDyingGain() {
	all().forEach(DeployedCard::computeDyingGain);
	counters(all()).forEach(counters::sumDyingGain);

	model.addGold(counters.getDieGold());
	model.addLegend(counters.getDieLegend());
    }

    public void computeValues() {
	counters.setGoldGain(2);

	all().forEach(DeployedCard::computeGoldGain);
	counters(all()).forEach(counters::sumGold);

	all().forEach(DeployedCard::computeValues);
	counters(all()).forEach(counters::sumValues);
    }

    public void computeWarGain() {
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

    public void doAge() {
	units().forEach(DeployedCard::doAge);
    }

    public Stream<DeployedCard> dyings() {
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

    public void gainGold() {
	model.addGold(counters.getGoldGain());
    }

    public int getCombat() {
	return counters.getCombat();
    }

    public int getCrystal() {
	return counters.getCrystal();
    }

    public int getFood() {
	return counters.getFood();
    }

    public int getGoldGain() {
	return counters.getGoldGain();
    }

    public int getVictoriousWar() {
	return counters.getVictoriousWar();
    }

    public int getWood() {
	return counters.getFood();
    }

    public boolean hasInputActions() {
	return !model.getInputActions().isEmpty();
    }

    public void preserveFromDeath(CardPosition position) {
	Iterator<DeployedCard> it = dying.iterator();
	while (it.hasNext()) {
	    DeployedCard next = it.next();
	    if (next.getPosition().equals(position))
		it.remove();
	}

    }

    public void removeDead() {
	dyings().map(DeployedCard::getPosition).map(this::find).forEach(CardSlot::clear);
	dying.clear();
    }

    public void resetCounters() {
	counters = new PlayerCounters();
	all().forEach(DeployedCard::resetCounters);
    }

    public void setVictoriousWar(int victoriousWar) {
	counters.setVictoriousWar(victoriousWar);
    }

    public Stream<DeployedCard> units() {
	return asDeployedCard(Stream.concat(front.stream(), back.stream()));
    }
}
