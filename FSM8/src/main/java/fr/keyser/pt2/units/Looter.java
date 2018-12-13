package fr.keyser.pt2.units;

import fr.keyser.pt2.BoardAccessor;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.PlugableInt;

public class Looter extends Unit {

    private final PlugableInt victory = new PlugableInt();

    public Looter() {
	super(1);
	combat = ConstInt.THREE;
	warGoldGain = victory;
    }

    @Override
    public void setBoardAccessor(BoardAccessor accessor) {
	victory.setSupplier(accessor.board(LocalBoard::getVictory));
    }
}
