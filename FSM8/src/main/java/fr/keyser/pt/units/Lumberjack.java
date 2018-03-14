package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Lumberjack extends Unit {

    public Lumberjack() {
	super(essence(0)
		.combat(IntValue.constant(2))
	        .wood(IntValue.ONE));
    }

}
