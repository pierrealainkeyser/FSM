package fr.keyser.pt.units;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;
import fr.keyser.pt.effects.DoAgeEffect;

public final class Daemon extends Unit {

    public Daemon() {
	super(essence(2)
	        .ageLegend(IntValue.choice(DeployedCard.hasAgeToken(1).and(DeployedCard::willDie), IntValue.constant(-3), IntValue.ZERO))
	        .combat(IntValue.constant(7))
	        .effect(DeployedCard.INITIAL_DEPLOY_FIRST, DoAgeEffect.INSTANCE));
    }

}
