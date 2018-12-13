package fr.keyser.pt2.units;

import fr.keyser.pt2.BoardAccessor;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.PlugableInt;

public class Chiromancian extends Unit {

    private final PlugableInt dyingAgeToken = new PlugableInt();

    public Chiromancian() {
	super(2);
	combat = ConstInt.ONE;
	ageLegend = dyingAgeToken;
    }

    @Override
    public void setBoardAccessor(BoardAccessor accessor) {
	dyingAgeToken.setSupplier(accessor.board(LocalBoard::getDyingAgeToken));
    }
}
