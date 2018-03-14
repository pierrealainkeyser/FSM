package fr.keyser.pt.units;

import fr.keyser.pt.BooleanValue;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Dragon extends Unit {

    public Dragon() {
	super(essence(3)
	        .combat(IntValue.choice(BooleanValue.HAS_CRYSTAL, IntValue.constant(7), IntValue.ONE)));
    }

}
