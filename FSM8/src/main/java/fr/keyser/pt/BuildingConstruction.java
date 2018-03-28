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

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((building == null) ? 0 : building.hashCode());
	result = prime * result + goldCost;
	result = prime * result + ((level == null) ? 0 : level.hashCode());
	result = prime * result + ((type == null) ? 0 : type.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	BuildingConstruction other = (BuildingConstruction) obj;
	if (building == null) {
	    if (other.building != null)
		return false;
	} else if (!building.equals(other.building))
	    return false;
	if (goldCost != other.goldCost)
	    return false;
	if (level != other.level)
	    return false;
	if (type != other.type)
	    return false;
	return true;
    }
}
