package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;

public class Chiromancian extends Unit {
    public Chiromancian() {
	super(2);
	combat = ConstInt.ONE;
	ageLegend = mapInt(LocalBoard::getDyingAgeToken);
    }
}
