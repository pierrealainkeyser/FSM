package fr.keyser.pt.units;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;
import fr.keyser.pt.effects.UpgradeBuildingToLevel2;

public final class Golem extends Unit {

    public Golem() {
	super(essence(2)
	        .combat(IntValue.constant(3))
	        .effect(DeployedCard.INITIAL_DEPLOY_SYNCHRONOUS_LAST, UpgradeBuildingToLevel2.INSTANCE));
    }

}
