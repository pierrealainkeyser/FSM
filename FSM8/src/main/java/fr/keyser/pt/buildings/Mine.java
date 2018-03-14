package fr.keyser.pt.buildings;

import fr.keyser.pt.Building;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;

public class Mine extends Building {
    public Mine() {
	super(essence(cost().wood(1), cost().wood(1).crystal(1))
	        .crystal(IntValue.ONE)
	        .warLegend(IntValue.choice(DeployedCard::isLevel2,
	                IntValue.constant(2).mult(IntValue.CRYSTAL),
	                IntValue.ZERO)));
    }
}
