package fr.keyser.pt.units;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Veteran extends Unit {

    public Veteran() {
	super(essence(0)
	        .combat(IntValue.constant(3))
	        .effect(DeployedCard.INITIAL_DEPLOY, DeployedCard::doAge));
    }

}
