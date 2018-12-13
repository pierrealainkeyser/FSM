package fr.keyser.pt2.buildings;

import fr.keyser.pt2.prop.ConstInt;

public class Temple extends Building {

    public Temple() {
	super(cost().crystal(1).alternate(cost().gold(2)), cost().crystal(2).alternate(cost().gold(3)));
	warGoldGain = ConstInt.TWO;
	warLegend = ConstInt.TWO.when(getLevel2());
    }

}
