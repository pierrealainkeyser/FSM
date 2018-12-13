package fr.keyser.pt.effects;

import fr.keyser.pt.DeployedCard;

public class AgeEveryoneButMe implements SelfEffect {

    public static final AgeEveryoneButMe INSTANCE = new AgeEveryoneButMe();

    private AgeEveryoneButMe() {

    }

    @Override
    public void apply(DeployedCard card) {
	card.getPlayer().units().filter(c -> c != card).forEach(DeployedCard::doAge);
    }
}
