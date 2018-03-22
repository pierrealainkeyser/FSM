package fr.keyser.pt.units;

import fr.keyser.pt.AsyncSpecialEffect;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.Unit;
import fr.keyser.pt.effects.PreserveFromDeathEffect;

public final class Mystic extends Unit {

    public Mystic() {
	super(essence(0)
	        .effect(DeployedCard.AGING, new AsyncSpecialEffect(new PreserveFromDeathEffect())));
    }

}
