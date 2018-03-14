package fr.keyser.pt.units;

import fr.keyser.pt.BooleanValue;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class GreatSnake extends Unit {

    public GreatSnake() {
	super(essence(1)
	        .mayCombat(BooleanValue.card(DeployedCard.hasAgeToken(0)))
	        .gold(IntValue.choice(DeployedCard.hasAgeToken(0), IntValue.ZERO, IntValue.constant(3)))
	        .combat(IntValue.constant(4)));
    }

}
