package fr.keyser.pt2.units;

import fr.keyser.pt2.prop.ConstInt;

public class Sculptrice extends Unit {
    public Sculptrice() {
	super(ConstInt.ZERO);
	ageLegend = ConstInt.THREE.when(getWillDie());
    }
}
