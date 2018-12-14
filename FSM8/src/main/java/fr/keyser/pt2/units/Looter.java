package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;

public class Looter extends Unit {
    public Looter() {
	super(1);
	combat = ConstInt.THREE;
	warGoldGain = mapInt(LocalBoard::getVictory);
    }
}
