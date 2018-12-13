package fr.keyser.pt2.buildings;

import fr.keyser.pt2.prop.ConstInt;

public class Temple extends Building {

    public Temple() {
	warGoldGain = ConstInt.TWO;
	warLegend = ConstInt.TWO.when(isLevel2());
    }

}
