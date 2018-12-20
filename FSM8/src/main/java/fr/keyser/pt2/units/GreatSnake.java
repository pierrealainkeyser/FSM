package fr.keyser.pt2.units;

import fr.keyser.pt2.prop.BoolSupplier;
import fr.keyser.pt2.prop.ConstInt;

public class GreatSnake extends Unit {
    public GreatSnake() {
	super(ConstInt.ONE);
	BoolSupplier hasAge = getAge().gte(ConstInt.ONE);

	combat = ConstInt.FOUR;
	payGoldGain = ConstInt.TWO.when(hasAge);
	mayCombat = mayCombat.and(hasAge.not());
    }
}
