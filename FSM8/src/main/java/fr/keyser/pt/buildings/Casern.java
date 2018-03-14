package fr.keyser.pt.buildings;

import fr.keyser.pt.BooleanValue;
import fr.keyser.pt.Building;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;

public class Casern extends Building {
    public Casern() {
	super(essence(cost().wood(1), cost().wood(1).food(1))
	        .mayCombat(BooleanValue.TRUE)
	        .combat(IntValue.WOOD)
	        .warLegend(IntValue.choice(DeployedCard::isLevel2,
	                IntValue.player(p -> (int) p.units().filter(u -> u.getCombat() >= 3).count()),
	                IntValue.ZERO)));
    }
}
