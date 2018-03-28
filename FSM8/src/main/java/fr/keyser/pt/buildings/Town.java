package fr.keyser.pt.buildings;

import fr.keyser.pt.Building;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;

public class Town extends Building {
    public Town() {
	super(essence(cost().wood(1), cost().wood(2))
	        .food(IntValue.ONE)
	        .wood(IntValue.ONE)
	        .crystal(IntValue.choice(DeployedCard::isLevel2, IntValue.ONE, IntValue.ZERO)));
    }
}
