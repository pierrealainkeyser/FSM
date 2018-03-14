package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Miner extends Unit {

    public Miner() {
	super(essence(1)
	        .crystal(IntValue.ONE)
	        .combat(IntValue.constant(2)));
    }

}
