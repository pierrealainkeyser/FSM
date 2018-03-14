package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class General extends Unit {

    public General() {
	super(essence(2)
	        .warLegend(IntValue.VICTORIOUS_WAR)
	        .combat(IntValue.constant(4)));
    }

}
