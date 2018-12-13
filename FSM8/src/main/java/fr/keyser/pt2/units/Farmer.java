package fr.keyser.pt2.units;

import fr.keyser.pt2.prop.ConstInt;

public class Farmer extends Unit {

    public Farmer() {
	super(0);
	combat = ConstInt.ONE;
	food = ConstInt.ONE;
    }
}
