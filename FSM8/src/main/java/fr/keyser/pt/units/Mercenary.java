package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Mercenary extends Unit {

    public Mercenary() {
	super(essence(0)
	        .combat(IntValue.constant(2).plus(IntValue.ALL_AGE_TOKEN)));
    }

}
