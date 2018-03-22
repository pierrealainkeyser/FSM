package fr.keyser.pt.units;

import fr.keyser.pt.AsyncSpecialEffect;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;
import fr.keyser.pt.effects.DropAgeTokenEffect;

public final class TimeMistress extends Unit {

    public TimeMistress() {
	super(essence(0)
	        .effect(DeployedCard.INITIAL_DEPLOY, new AsyncSpecialEffect(new DropAgeTokenEffect(IntValue.constant(2)))));
    }

}
