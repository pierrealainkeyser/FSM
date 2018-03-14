package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Notable extends Unit {

    public Notable() {
	super(essence(2)
	        .wood(IntValue.ONE));
    }

}
