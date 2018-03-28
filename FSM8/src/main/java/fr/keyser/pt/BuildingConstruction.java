package fr.keyser.pt;

public class BuildingConstruction {

    public enum BuildType {
	BUILD, UPGRADE
    }

    private MetaCard building;

    private int goldCost;

    private BuildingLevel level;

    private BuildType type;

    public BuildingConstruction() {
    }

    public BuildingConstruction(MetaCard building, int goldCost, BuildType type, BuildingLevel level) {
	this.building = building;
	this.goldCost = goldCost;
	this.type = type;
	this.level = level;
    }

    public MetaCard getBuilding() {
        return building;
    }

    public int getGoldCost() {
	return goldCost;
    }

    public BuildingLevel getLevel() {
	return level;
    }

    public BuildType getType() {
	return type;
    }

    public void setBuilding(MetaCard building) {
        this.building = building;
    }

    public void setGoldCost(int goldCost) {
	this.goldCost = goldCost;
    }

    public void setLevel(BuildingLevel level) {
	this.level = level;
    }

    public void setType(BuildType type) {
	this.type = type;
    }

    @Override
    public String toString() {
	return "BuildingConstruction [building=" + building + ", goldCost=" + goldCost + ", level=" + level + ", type=" + type + "]";
    }
}
