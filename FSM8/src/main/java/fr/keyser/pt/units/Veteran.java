package fr.keyser.pt.units;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;
import fr.keyser.pt.effects.DoAgeEffect;

public final class Veteran extends Unit {

    public Veteran() {
	super(essence(0)
	        .combat(IntValue.constant(3))
	        .effect(DeployedCard.INITIAL_DEPLOY_FIRST, DoAgeEffect.INSTANCE));
    }

}
