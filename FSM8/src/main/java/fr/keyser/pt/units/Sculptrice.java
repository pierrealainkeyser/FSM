package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Sculptrice extends Unit {

    public Sculptrice() {
	super(essence(0)
	        .ageLegend(IntValue.constant(3)));
    }

}
