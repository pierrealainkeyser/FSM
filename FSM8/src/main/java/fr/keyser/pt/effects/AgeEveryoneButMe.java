package fr.keyser.pt.effects;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.SpecialEffect;

public class AgeEveryoneButMe implements SpecialEffect {

    public static final SpecialEffect INSTANCE = new AgeEveryoneButMe();

    private AgeEveryoneButMe() {

    }

    @Override
    public void apply(DeployedCard card) {
	card.getPlayer().units().filter(c -> c != card).forEach(DeployedCard::doAge);
    }
}
