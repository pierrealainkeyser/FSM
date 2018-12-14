package fr.keyser.pt2.buildings;

import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;

public class Mine extends Building {
    public Mine() {
	super(cost().wood(1), cost().wood(1).crystal(1));
	crystal = ConstInt.ONE;
	warLegend = mapInt(LocalBoard::getCrystal).mult(ConstInt.TWO).when(getLevel2());
    }
}
