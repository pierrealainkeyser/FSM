package fr.keyser.pt2.units;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.IntSupplier;

public abstract class Unit extends Card {
    protected Unit(IntSupplier cost) {
	this.cost = cost;
	combat = ConstInt.ONE;
    }
}
