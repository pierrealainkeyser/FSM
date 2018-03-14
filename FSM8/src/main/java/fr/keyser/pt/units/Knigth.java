package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Knigth extends Unit {

    public Knigth() {
	super(essence(1)
	        .combat(IntValue.constant(4)));
    }

}
