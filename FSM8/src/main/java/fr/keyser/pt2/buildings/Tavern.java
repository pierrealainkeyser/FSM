package fr.keyser.pt2.buildings;

import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.IntSupplier;

public class Tavern extends Building {
    public Tavern() {
	super(cost().food(1), cost().wood(1).food(1));
	IntSupplier totalFood = mapInt(LocalBoard::getFood);
	warGoldGain = totalFood;
	warLegend = totalFood.when(getLevel2());
    }
}
