package fr.keyser.pt.units;

import fr.keyser.pt.BooleanValue;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Archer extends Unit {

    public Archer() {
	super(essence(1)
	        .mayCombat(BooleanValue.TRUE)
	        .combat(IntValue.constant(2)));
    }

}
