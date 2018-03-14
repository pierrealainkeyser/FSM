package fr.keyser.pt.units;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Colossus extends Unit {

    public Colossus() {
	super(essence(2)
	        .warLegend(IntValue.VICTORIOUS_WAR)
	        .combat(IntValue.choice(DeployedCard.hasALeastAgeToken(1), IntValue.constant(7), IntValue.ONE)));
    }

}
