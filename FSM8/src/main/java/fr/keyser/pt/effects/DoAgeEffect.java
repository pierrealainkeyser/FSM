package fr.keyser.pt.effects;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.SpecialEffect;

public class DoAgeEffect implements SpecialEffect {

    public static final SpecialEffect INSTANCE = new DoAgeEffect();

    private DoAgeEffect() {

    }

    @Override
    public void apply(DeployedCard card) {
	card.doAge();
    }
}
