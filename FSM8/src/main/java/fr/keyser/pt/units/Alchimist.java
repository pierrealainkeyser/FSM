package fr.keyser.pt.units;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;
import fr.keyser.pt.effects.DropAgeTokenEffect;

public final class Alchimist extends Unit {

    public Alchimist() {
	super(essence(2)
	        .gold(IntValue.WOOD)
	        .warLegend(IntValue.CRYSTAL)
	        .effect(DeployedCard.DEPLOY, new DropAgeTokenEffect(IntValue.FOOD)));
    }

}
