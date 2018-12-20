package fr.keyser.pt2.units;

import fr.keyser.pt2.prop.ConstInt;

public class Monkey extends Unit {
    public Monkey() {
	super(ConstInt.MINUS_ONE);
	ageLegend = getDyingAgeToken();
    }
}
