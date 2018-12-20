package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;

public class Manticore extends Unit {
    public Manticore() {
	super(ConstInt.TWO);
	combat = ConstInt.TWO.add(mapInt(LocalBoard::getFood).mult(ConstInt.TWO));
    }
}
