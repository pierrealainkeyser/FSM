package fr.keyser.pt2.units;

import fr.keyser.pt2.prop.ConstInt;

public final class Chapman extends Unit {
    public Chapman() {
	super(1);
	wood = ConstInt.ONE;
	payGoldGain = ConstInt.ONE;
    }
}
