package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class ForestChildren extends Unit {

    public ForestChildren() {
	super(essence(1)
	        .food(IntValue.ONE)
	        .wood(IntValue.ONE));
    }

}
