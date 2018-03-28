package fr.keyser.pt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import fr.keyser.pt.BuildingConstruction.BuildType;
import fr.keyser.pt.buildings.Casern;
import fr.keyser.pt.buildings.Mine;
import fr.keyser.pt.buildings.Tavern;
import fr.keyser.pt.buildings.Temple;
import fr.keyser.pt.buildings.Town;

public class TestBuildingPlanner {

    @Test
    public void testNominal() {

	MetaCardBuilder b = new MetaCardBuilder();
	List<MetaCard> bluePrints = Stream.of(new Town(), new Tavern(), new Casern(), new Temple(), new Mine()).map(b::meta)
	        .collect(Collectors.toList());
	MetaCard town = bluePrints.get(0);
	MetaCard tavern = bluePrints.get(1);
	MetaCard casern = bluePrints.get(2);
	MetaCard temple = bluePrints.get(3);
	MetaCard mine = bluePrints.get(4);

	BuildingPlanner planner = new BuildingPlanner(0, 1, 2, 5, Stream.empty());
	List<BuildingConstruction> buildable = planner.compute(bluePrints);

	Set<BuildingConstruction> expected = new HashSet<>();
	expected.add(new BuildingConstruction(town, 0, BuildType.BUILD, BuildingLevel.LEVEL1));
	expected.add(new BuildingConstruction(tavern, 0, BuildType.BUILD, BuildingLevel.LEVEL1));
	expected.add(new BuildingConstruction(casern, 0, BuildType.BUILD, BuildingLevel.LEVEL1));
	expected.add(new BuildingConstruction(casern, 0, BuildType.BUILD, BuildingLevel.LEVEL2));
	expected.add(new BuildingConstruction(temple, 2, BuildType.BUILD, BuildingLevel.LEVEL1));
	expected.add(new BuildingConstruction(temple, 5, BuildType.BUILD, BuildingLevel.LEVEL2));
	expected.add(new BuildingConstruction(mine, 0, BuildType.BUILD, BuildingLevel.LEVEL1));

	Assert.assertTrue(buildable.containsAll(expected));
	Assert.assertTrue(expected.containsAll(buildable));

    }

}
