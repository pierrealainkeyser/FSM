package fr.keyser.pt.effects;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.SpecialEffect;

public class RecomputeValueIfAgedEffect implements SpecialEffect {

    public static final SpecialEffect INSTANCE = new RecomputeValueIfAgedEffect();

    private RecomputeValueIfAgedEffect() {

    }

    @Override
    public void apply(DeployedCard card) {
	if (card.getAgeToken() > 0)
	    card.recomputeValues();
    }
}
