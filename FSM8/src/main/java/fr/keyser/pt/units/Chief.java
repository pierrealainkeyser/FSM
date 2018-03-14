package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Chief extends Unit {

    public Chief() {
	super(essence(0)
	        .gold(IntValue.FOOD));
    }

}
