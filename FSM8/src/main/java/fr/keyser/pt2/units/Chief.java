package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;

public class Chief extends Unit {
    public Chief() {
	super(0);
	combat = ConstInt.ONE;
	payGoldGain = mapInt(LocalBoard::getFood);
    }
}
