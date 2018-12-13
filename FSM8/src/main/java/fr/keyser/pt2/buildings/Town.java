package fr.keyser.pt2.buildings;

import fr.keyser.pt2.prop.ConstInt;

public class Town extends Building {

    public Town() {
	super(cost().wood(1), cost().wood(2));
	food = ConstInt.ONE;
	wood = ConstInt.ONE;
	crystal = ConstInt.ONE.when(getLevel2());
    }
}
