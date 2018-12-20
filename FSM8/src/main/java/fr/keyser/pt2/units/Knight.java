package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;

public class Knight extends Unit {
    public Knight() {
	super(ConstInt.TWO);
	combat = ConstInt.FOUR;
	warLegend = mapInt(LocalBoard::getVictory);
    }
}
