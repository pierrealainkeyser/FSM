package fr.keyser.pt2.units;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.prop.ConstInt;

public abstract class Unit extends Card {

    private final int cost;

    protected Unit(int cost) {
	this.cost = cost;
	combat = ConstInt.ONE;
    }

    public int getCost() {
	return cost;
    }
}
