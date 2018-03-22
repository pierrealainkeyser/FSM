package fr.keyser.pt.units;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;
import fr.keyser.pt.effects.AgeEveryoneButMe;

public final class Necromancer extends Unit {

    public Necromancer() {
	super(essence(1)
	        .combat(IntValue.constant(4))
	        .effect(DeployedCard.INITIAL_DEPLOY, AgeEveryoneButMe.INSTANCE));
    }

}
