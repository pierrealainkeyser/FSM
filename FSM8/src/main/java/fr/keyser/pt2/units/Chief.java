package fr.keyser.pt2.units;

import fr.keyser.pt2.BoardAccessor;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.PlugableInt;

public class Chief extends Unit {

    private final PlugableInt totalFood = new PlugableInt();

    public Chief() {
	combat = ConstInt.ONE;
	payGoldGain = totalFood;
    }

    @Override
    public void setBoardAccessor(BoardAccessor accessor) {
	totalFood.setSupplier(accessor.board(LocalBoard::getFood));
    }
}
