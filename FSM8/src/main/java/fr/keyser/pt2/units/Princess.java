package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;

public class Princess extends Unit {
    public Princess() {
	super(5);
	warLegend = mapInt(LocalBoard::getBuildingWarLegend);
    }
}
