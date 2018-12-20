package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;

public class Troll extends Unit {
    public Troll() {
	super(ConstInt.TWO);
	combat = ConstInt.TWO.add(mapInt(LocalBoard::getPayGoldGain));
    }
}
