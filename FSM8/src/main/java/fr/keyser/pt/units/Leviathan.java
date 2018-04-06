package fr.keyser.pt.units;

import fr.keyser.pt.BooleanValue;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;
import fr.keyser.pt.effects.RecomputeValueIfAgedEffect;

public final class Leviathan extends Unit {

    public Leviathan() {
	super(essence(3)
	        .mayCombat(BooleanValue.card(DeployedCard.hasAgeToken(0)))
	        .food(IntValue.choice(DeployedCard.hasAgeToken(0), IntValue.ZERO, IntValue.constant(2)))
	        .combat(IntValue.constant(4).plus(IntValue.FOOD.mult(IntValue.constant(2))))
	        .effect(DeployedCard.DEPLOY, RecomputeValueIfAgedEffect.INSTANCE));
    }

}
