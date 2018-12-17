package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;

public class Chief extends Unit {
    public Chief() {
	super(0);
	payGoldGain = mapInt(LocalBoard::getFood);
    }
}
