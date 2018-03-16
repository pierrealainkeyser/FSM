package fr.keyser.pt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import fr.keyser.pt.BuildingConstruction.BuildType;

public class BuildingPlanner {

    public static interface ProvideWoodForGold {

    }

    public static interface IgnoreBuildingBaseCost {

    }

    public static interface ProvideAnyResourceForGold {

    }

    private final static Comparator<RawBuildingCost> MIN_COST = Comparator.comparingInt(RawBuildingCost::getGold);
    private RawBuildingCost available;

    private int baseCost;

    private boolean anyResourceForGold;

    private boolean ignoreBuildingBaseCost;

    private boolean woodForGold;

    private final Set<String> buildLevel1 = new HashSet<>();

    private final Set<String> buildLevel2 = new HashSet<>();

    BuildingPlanner(PlayerBoardModel model, PlayerCounters counters, Stream<DeployedCard> cards) {
	available = new RawBuildingCost()
	        .crystal(counters.getCrystal())
	        .food(counters.getFood())
	        .wood(counters.getFood())
	        .gold(model.getGold());

	AtomicInteger buildingCount = new AtomicInteger(0);

	cards.forEach(c -> {
	    Card u = c.getCard();
	    if (u instanceof IgnoreBuildingBaseCost)
		ignoreBuildingBaseCost = true;
	    else if (u instanceof ProvideAnyResourceForGold)
		anyResourceForGold = true;
	    else if (u instanceof ProvideWoodForGold)
		woodForGold = true;

	    if (u instanceof Building) {
		buildingCount.incrementAndGet();
		if (BuildingLevel.LEVEL1 == c.getLevel())
		    buildLevel1.add(u.getName());
		else
		    buildLevel2.add(u.getName());
	    }
	});
	baseCost = 2 * buildingCount.get();
    }

    List<BuildingConstruction> compute(List<MetaCard> bluePrints) {
	List<BuildingConstruction> plan = new ArrayList<>();
	for (MetaCard meta : bluePrints) {
	    Building bluePrint = (Building) meta.getCard();
	    String name = bluePrint.getName();
	    if (buildLevel1.contains(name)) {

		upgrade(bluePrint).ifPresent(r -> {
		    plan.add(new BuildingConstruction(meta, r.getGold(), BuildType.UPGRADE, BuildingLevel.LEVEL2));
		});

	    } else if (!buildLevel2.contains(name)) {
		buildLevel1(bluePrint).ifPresent(r -> {
		    plan.add(new BuildingConstruction(meta, r.getGold(), BuildType.BUILD, BuildingLevel.LEVEL1));
		});

		buildLevel2(bluePrint).ifPresent(r -> {
		    plan.add(new BuildingConstruction(meta, r.getGold(), BuildType.BUILD, BuildingLevel.LEVEL2));
		});
	    }
	}
	return plan;
    }

    private RawBuildingCost buildCost(RawBuildingCost cost) {
	if (ignoreBuildingBaseCost)
	    return goldGost(cost);
	else
	    return goldGost(cost.clone().gold(cost.getGold() + baseCost));
    }

    private Optional<RawBuildingCost> buildLevel1(Building building) {
	List<RawBuildingCost> cost = new ArrayList<>();

	RawBuildingCost level1 = building.getLevel1();
	cost.add(buildCost(level1));
	RawBuildingCost alterned = level1.getAlternate();
	if (alterned != null)
	    cost.add(buildCost(alterned));

	return cost.stream().filter(Objects::nonNull).min(MIN_COST);
    }

    private Optional<RawBuildingCost> buildLevel2(Building building) {
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
	if (anyResourceForGold || woodForGold) {
	    if (anyResourceForGold) {
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

    private Optional<RawBuildingCost> upgrade(Building building) {
	List<RawBuildingCost> cost = new ArrayList<>();

	RawBuildingCost level2 = building.getLevel2();
	cost.add(goldGost(level2));
	RawBuildingCost alterned = level2.getAlternate();
	if (alterned != null)
	    cost.add(goldGost(alterned));

	return cost.stream().filter(Objects::nonNull).min(MIN_COST);

    }

}