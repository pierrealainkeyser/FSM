package fr.keyser.pt;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

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

	BuildingPlanner planner = new BuildingPlanner(0, 1, 2, 5, Stream.empty());
	List<BuildingConstruction> buildable = planner.compute(bluePrints);
	for (BuildingConstruction bc : buildable)
	    System.out.println(bc);
    }

}
