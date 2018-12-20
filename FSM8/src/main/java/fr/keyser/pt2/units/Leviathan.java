package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.BoolSupplier;
import fr.keyser.pt2.prop.ConstInt;

public class Leviathan extends Unit {
    public Leviathan() {
	super(ConstInt.TWO);
	BoolSupplier hasAge = getAge().gte(ConstInt.ONE);

	combat = ConstInt.FOUR.add(mapInt(LocalBoard::getFood).mult(ConstInt.TWO));
	food = ConstInt.TWO.when(hasAge);
	mayCombat = mayCombat.and(hasAge.not());
    }
}
