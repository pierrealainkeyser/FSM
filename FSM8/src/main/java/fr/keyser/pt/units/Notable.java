package fr.keyser.pt.units;

import fr.keyser.pt.BuildingPlanner.IgnoreBuildingBaseCost;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Notable extends Unit implements IgnoreBuildingBaseCost {

    public Notable() {
	super(essence(2)
	        .wood(IntValue.ONE));
    }

}
