package fr.keyser.pt2.units;

import fr.keyser.pt2.BoardAccessor;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.PlugableInt;

public class Knight extends Unit {

    private final PlugableInt victory = new PlugableInt();

    public Knight() {
	combat = ConstInt.FOUR;
	warLegend = victory;
    }

    @Override
    public void setBoardAccessor(BoardAccessor accessor) {
	victory.setSupplier(accessor.board(LocalBoard::getVictory));
    }
}
