package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Treeman extends Unit {

    public Treeman() {
	super(essence(1)
	        .food(IntValue.ONE)
	        .combat(IntValue.constant(3)));
    }

}
