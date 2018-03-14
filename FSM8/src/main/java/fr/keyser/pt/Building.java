package fr.keyser.pt;

public class Building extends Card {
    protected static BuildingEssence essence(RawBuildingCost level1, RawBuildingCost level2) {
	return new BuildingEssence(level1, level2);
    }

    protected static RawBuildingCost cost() {
	return new RawBuildingCost();
    }

    private final RawBuildingCost level1;

    private final RawBuildingCost level2;

    protected Building(BuildingEssence e) {
	super(e);
	level1 = e.level1;
	level2 = e.level2;
    }

    public final RawBuildingCost getLevel1() {
	return level1;
    }

    public final RawBuildingCost getLevel2() {
	return level2;
    }
}
