package fr.keyser.pt2.units;

import fr.keyser.pt2.BoardAccessor;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.PlugableInt;

public class Manticore extends Unit {

    private final PlugableInt totalFood = new PlugableInt();

    public Manticore() {
	super(2);
	combat = ConstInt.TWO.add(totalFood.mult(ConstInt.TWO));
    }

    @Override
    public void setBoardAccessor(BoardAccessor accessor) {
	totalFood.setSupplier(accessor.board(LocalBoard::getFood));
    }
}
