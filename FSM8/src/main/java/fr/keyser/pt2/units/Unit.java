package fr.keyser.pt2.units;

import fr.keyser.pt2.Card;

public abstract class Unit extends Card {

    private final int cost;

    protected Unit(int cost) {
	this.cost = cost;
    }

    public int getCost() {
	return cost;
    }
}
