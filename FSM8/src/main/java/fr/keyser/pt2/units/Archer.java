package fr.keyser.pt2.units;

import fr.keyser.pt2.prop.ConstBool;
import fr.keyser.pt2.prop.ConstInt;

public final class Archer extends Unit {
    public Archer() {
	super(ConstInt.ONE);
	mayCombat = ConstBool.TRUE;
	combat = ConstInt.TWO;
    }
}
