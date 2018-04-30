package fr.keyser.pt.effects;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.SpecialEffect;

public class NotifyShiftShapeEffect implements SpecialEffect {

    public static final SpecialEffect INSTANCE = new NotifyShiftShapeEffect();

    private NotifyShiftShapeEffect() {

    }

    @Override
    public void apply(DeployedCard card) {
	card.clearShapeShifted();
	card.doAge();

    }
}
