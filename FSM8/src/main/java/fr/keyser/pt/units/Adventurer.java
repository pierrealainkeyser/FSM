package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Adventurer extends Unit {

    public Adventurer() {
	super(essence(0)
	        .crystal(IntValue.AGE)
	        .combat(IntValue.constant(2)));
    }

}
