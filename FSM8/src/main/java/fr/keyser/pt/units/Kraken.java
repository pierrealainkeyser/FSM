package fr.keyser.pt.units;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;
import fr.keyser.pt.effects.AgeEveryoneButMe;

public final class Kraken extends Unit {

    public Kraken() {
	super(essence(6)
	        .combat(IntValue.constant(9))
	        .deployLegend(
	                IntValue.choice(DeployedCard::isInitialDeploy,
	                        IntValue.constant(-3).mult(IntValue.LEVEL2_BUILDING),
	                        IntValue.ZERO))
	        .dieLegend(IntValue.DYING_AGE_TOKEN)
	        .effect(DeployedCard.INITIAL_DEPLOY_FIRST, AgeEveryoneButMe.INSTANCE));
    }
}
