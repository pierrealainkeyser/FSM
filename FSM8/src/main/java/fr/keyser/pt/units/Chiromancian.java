package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Chiromancian extends Unit {

    public Chiromancian() {
	super(essence(2)
	        .ageLegend(IntValue.DYING_AGE_TOKEN));
    }

}
