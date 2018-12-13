package fr.keyser.pt2.units;

import fr.keyser.pt2.BoardAccessor;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.BoolSupplier;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.PlugableInt;

public class Leviathan extends Unit {

    private final PlugableInt totalFood = new PlugableInt();

    public Leviathan() {
	super(2);
	BoolSupplier hasAge = getAge().gte(ConstInt.ONE);

	combat = ConstInt.FOUR.add(totalFood.mult(ConstInt.TWO));
	food = ConstInt.TWO.when(hasAge);
	mayCombat = mayCombat.and(hasAge.not());
    }

    @Override
    public void setBoardAccessor(BoardAccessor accessor) {
	totalFood.setSupplier(accessor.board(LocalBoard::getFood));
    }
}
