package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;

public class Princess extends Unit {
    public Princess() {
	super(ConstInt.FIVE);
	warLegend = mapInt(LocalBoard::getBuildingWarLegend);
    }
}
