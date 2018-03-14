package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Farmer extends Unit {

    public Farmer() {
	super(essence(0)
	        .food(IntValue.ONE));
    }

}
