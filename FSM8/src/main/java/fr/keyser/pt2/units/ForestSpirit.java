package fr.keyser.pt2.units;

import fr.keyser.pt2.prop.ConstInt;

public final class ForestSpirit extends Unit {
    public ForestSpirit() {
	super(3);
	combat = ConstInt.THREE;
	wood = ConstInt.THREE;
    }
}
