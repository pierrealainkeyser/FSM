package fr.keyser.pt2.buildings;

import fr.keyser.pt.RawBuildingCost;
import fr.keyser.pt2.Card;
import fr.keyser.pt2.prop.BoolSupplier;
import fr.keyser.pt2.prop.ConstInt;

public abstract class Building extends Card {

    protected static RawBuildingCost cost() {
	return new RawBuildingCost();
    }

    private final RawBuildingCost costLevel1;

    private final RawBuildingCost costLevel2;

    private final BoolSupplier level2 = getBuildLevel().gte(ConstInt.TWO);

    protected Building(RawBuildingCost costLevel1, RawBuildingCost costLevel2) {
	setBuildingLevel(1);
	this.costLevel1 = costLevel1;
	this.costLevel2 = costLevel2;
    }

    public void setBuildingLevel(int level) {
	getBuildLevel().setValue(level);
    }

    public BoolSupplier getLevel2() {
	return level2;
    }

    public RawBuildingCost getCostLevel1() {
        return costLevel1;
    }

    public RawBuildingCost getCostLevel2() {
        return costLevel2;
    }

}
