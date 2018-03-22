package fr.keyser.pt.units;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class CaveSpirit extends Unit {

    public CaveSpirit() {
	super(essence(1)
	        .crystal(IntValue.ONE)
	        .combat(IntValue.constant(4))
	        .effect(DeployedCard.INITIAL_DEPLOY, DeployedCard::doAge));
    }

}
