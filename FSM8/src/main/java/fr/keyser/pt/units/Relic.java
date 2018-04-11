package fr.keyser.pt.units;

import fr.keyser.pt.BooleanValue;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Relic extends Unit {

    public Relic() {
	super(essence(2)
	        .endOfGameLegend(IntValue.constant(2).mult(DeployedCard::getAgeToken))
	        .deathCondition(BooleanValue.FALSE));
    }

}
