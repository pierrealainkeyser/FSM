package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Chapman extends Unit {
    public Chapman() {
	super(essence(1)
		.gold(IntValue.ONE)
	        .wood(IntValue.ONE));
    }

}
