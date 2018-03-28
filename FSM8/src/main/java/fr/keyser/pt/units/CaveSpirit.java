package fr.keyser.pt.units;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;
import fr.keyser.pt.effects.DoAgeEffect;

public final class CaveSpirit extends Unit {

    public CaveSpirit() {
	super(essence(1)
	        .crystal(IntValue.ONE)
	        .combat(IntValue.constant(4))
	        .effect(DeployedCard.INITIAL_DEPLOY_FIRST,  DoAgeEffect.INSTANCE));
    }

}
