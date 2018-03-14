package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class ForestSpirit extends Unit {

    public ForestSpirit() {
	super(essence(3)
	        .combat(IntValue.constant(3))
	        .wood(IntValue.constant(3)));
    }

}
