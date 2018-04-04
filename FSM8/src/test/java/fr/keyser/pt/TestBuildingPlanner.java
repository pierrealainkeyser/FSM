package fr.keyser.pt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.keyser.pt.BuildingConstruction.BuildType;
import fr.keyser.pt.buildings.Casern;
import fr.keyser.pt.buildings.Mine;
import fr.keyser.pt.buildings.Tavern;
import fr.keyser.pt.buildings.Temple;
import fr.keyser.pt.buildings.Town;
import fr.keyser.pt.units.Notable;

public class TestBuildingPlanner {

    private static class MockInfo implements InstalledCardBuildPlanner {

	private final Card card;

	private final BuildingLevel level;

	public MockInfo(Unit unit) {
	    this.card = unit;
	    this.level = null;
	}

	public MockInfo(Building building, BuildingLevel level) {
	    this.card = building;
	    this.level = level;
	}

	@Override
	public Card getCard() {
	    return card;
	}

	@Override
	public BuildingLevel getLevel() {
	    return level;
	}

    }

    private static MockInfo level1(Building b) {
	return new MockInfo(b, BuildingLevel.LEVEL1);
    }

    private static MockInfo level2(Building b) {
	return new MockInfo(b, BuildingLevel.LEVEL2);
    }

    @Test
    public void testNotable() {

	MetaCardBuilder b = new MetaCardBuilder();
	List<MetaCard> bluePrints = Stream.of(new Town(), new Mine()).map(b::meta)
	        .collect(Collectors.toList());

	MetaCard mine = bluePrints.get(1);

	BuildingPlanner planner = new BuildingPlanner(1, 0, 2, 0, Stream.of(level2(new Town()), new MockInfo(new Notable())));
	List<BuildingConstruction> buildable = planner.compute(bluePrints);

	Set<BuildingConstruction> expected = new HashSet<>();
	expected.add(new BuildingConstruction(mine, 0, BuildType.BUILD, BuildingLevel.LEVEL1));
	expected.add(new BuildingConstruction(mine, 0, BuildType.BUILD, BuildingLevel.LEVEL2));

	Assertions.assertTrue(buildable.containsAll(expected));
	Assertions.assertTrue(expected.containsAll(buildable));

    }

    @Test
    public void testNominal() {

	MetaCardBuilder b = new MetaCardBuilder();
	List<MetaCard> bluePrints = Stream.of(new Town(), new Tavern(), new Casern(), new Temple(), new Mine()).map(b::meta)
	        .collect(Collectors.toList());
	MetaCard town = bluePrints.get(0);
	MetaCard tavern = bluePrints.get(1);
	MetaCard casern = bluePrints.get(2);
	MetaCard temple = bluePrints.get(3);

	BuildingPlanner planner = new BuildingPlanner(0, 1, 3, 6, Stream.of(level1(new Town()), level2(new Mine())));
	List<BuildingConstruction> buildable = planner.compute(bluePrints);

	Set<BuildingConstruction> expected = new HashSet<>();
	expected.add(new BuildingConstruction(town, 0, BuildType.UPGRADE, BuildingLevel.LEVEL2));
	expected.add(new BuildingConstruction(tavern, 4, BuildType.BUILD, BuildingLevel.LEVEL1));
	expected.add(new BuildingConstruction(casern, 4, BuildType.BUILD, BuildingLevel.LEVEL1));
	expected.add(new BuildingConstruction(casern, 4, BuildType.BUILD, BuildingLevel.LEVEL2));
	expected.add(new BuildingConstruction(temple, 6, BuildType.BUILD, BuildingLevel.LEVEL1));

	Assertions.assertTrue(buildable.containsAll(expected));
	Assertions.assertTrue(expected.containsAll(buildable));

    }

}
