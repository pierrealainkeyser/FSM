package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Looter extends Unit {

    public Looter() {
	super(essence(1)
		.gold(IntValue.VICTORIOUS_WAR)
	        .combat(IntValue.constant(3)));
    }

}
