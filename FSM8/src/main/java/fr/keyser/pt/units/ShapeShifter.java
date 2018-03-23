package fr.keyser.pt.units;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.Unit;
import fr.keyser.pt.effects.ShiftShapeEffect;

public final class ShapeShifter extends Unit {

    public ShapeShifter() {
	super(essence(0)
	        .effect(DeployedCard.PLAY, DeployedCard::doAge)
	        .effect(DeployedCard.PLAY, ShiftShapeEffect.INSTANCE));
    }

}
