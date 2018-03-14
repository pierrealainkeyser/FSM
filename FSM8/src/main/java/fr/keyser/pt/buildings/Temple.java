package fr.keyser.pt.buildings;

import fr.keyser.pt.Building;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;

public class Temple extends Building {
    public Temple() {
	super(essence(cost().crystal(1).alternate(cost().gold(2)), cost().crystal(2).alternate(cost().gold(3)))
	        .gold(IntValue.constant(2))
	        .warLegend(IntValue.choice(DeployedCard::isLevel2, IntValue.constant(2), IntValue.ZERO)));
    }
}
