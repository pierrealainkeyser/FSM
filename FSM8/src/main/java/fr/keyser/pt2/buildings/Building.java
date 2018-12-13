package fr.keyser.pt2.buildings;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.prop.BoolSupplier;
import fr.keyser.pt2.prop.ConstInt;

public abstract class Building extends Card {

    private BoolSupplier level2 = getBuildLevel().gte(ConstInt.TWO);

    public Building() {
	getBuildLevel().set(1);
    }

    public BoolSupplier isLevel2() {
	return level2;
    }

}
