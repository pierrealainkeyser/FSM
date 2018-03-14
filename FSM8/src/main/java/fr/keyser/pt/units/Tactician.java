package fr.keyser.pt.units;

import fr.keyser.pt.BooleanValue;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Tactician extends Unit {

    public Tactician() {
	super(essence(3)
	        .mayCombat(BooleanValue.TRUE)
	        .combat(IntValue.ONE.plus(IntValue.ALL_AGE_TOKEN)));
    }

}
