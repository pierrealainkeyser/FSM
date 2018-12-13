package fr.keyser.pt2.buildings;

import fr.keyser.pt2.prop.ConstInt;

public class Town extends Building {

    public Town() {
	food = ConstInt.ONE;
	wood = ConstInt.ONE;
	crystal = ConstInt.ONE.when(isLevel2());
    }

}
