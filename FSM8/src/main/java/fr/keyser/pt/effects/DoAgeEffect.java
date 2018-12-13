package fr.keyser.pt.effects;

import fr.keyser.pt.DeployedCard;

public class DoAgeEffect implements SelfEffect {

    public static final SelfEffect INSTANCE = new DoAgeEffect();

    private DoAgeEffect() {

    }

    @Override
    public void apply(DeployedCard card) {
	card.doAge();
    }
}
