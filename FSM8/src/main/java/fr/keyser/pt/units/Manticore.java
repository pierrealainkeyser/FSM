package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Manticore extends Unit {

    public Manticore() {
	super(essence(2)
	        .combat(IntValue.constant(2).plus(IntValue.FOOD.mult(IntValue.constant(2)))));
    }

}
