package fr.keyser.pt.units;

import fr.keyser.pt.BuildingPlanner.ProvideAnyResourceForGold;
import fr.keyser.pt.Unit;

public final class GrandArchitect extends Unit implements ProvideAnyResourceForGold {

    public GrandArchitect() {
	super(essence(1));
    }

}
