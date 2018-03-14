package fr.keyser.pt.buildings;

import fr.keyser.pt.Building;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;

public class Tavern extends Building {
    public Tavern() {
	super(essence(cost().food(1), cost().wood(1).food(1))
	        .gold(IntValue.FOOD)
	        .warLegend(IntValue.choice(DeployedCard::isLevel2, IntValue.FOOD, IntValue.ZERO)));
    }
}
