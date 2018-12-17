package fr.keyser.pt2.units;

import fr.keyser.pt2.prop.ConstInt;

public final class ForestChildren extends Unit {
    public ForestChildren() {
	super(1);
	food = ConstInt.ONE;
	wood = ConstInt.ONE;
    }
}
