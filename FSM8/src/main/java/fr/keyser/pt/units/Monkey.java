package fr.keyser.pt.units;

import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Monkey extends Unit {

    public Monkey() {
	super(essence(-1)
	        .dieGold(IntValue.AGE));
    }

}
