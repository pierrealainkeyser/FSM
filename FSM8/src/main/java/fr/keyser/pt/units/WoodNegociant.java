package fr.keyser.pt.units;

import fr.keyser.pt.BuildingPlanner.ProvideWoodForGold;
import fr.keyser.pt.Unit;

public final class WoodNegociant extends Unit implements ProvideWoodForGold {

    public WoodNegociant() {
	super(essence(0));
    }

}
