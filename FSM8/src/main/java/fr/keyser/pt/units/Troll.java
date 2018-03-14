package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Troll extends Unit {

    public Troll() {
	super(essence(3)
	        .combat(IntValue.constant(2).plus(IntValue.GOLD_GAIN)));
    }

}
