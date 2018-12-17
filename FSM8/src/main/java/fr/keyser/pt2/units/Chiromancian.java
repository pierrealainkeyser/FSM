package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;

public class Chiromancian extends Unit {
    public Chiromancian() {
	super(2);
	ageLegend = mapInt(LocalBoard::getDyingAgeToken);
    }
}
