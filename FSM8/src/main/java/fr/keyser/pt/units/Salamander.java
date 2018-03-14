package fr.keyser.pt.units;

import fr.keyser.pt.BooleanValue;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Salamander extends Unit {

    public Salamander() {
	super(essence(2)
	        .combat(IntValue.constant(4).plus(IntValue.AGE))
	        .deathCondition(BooleanValue.card(DeployedCard.hasALeastAgeToken(2))));
    }

}
