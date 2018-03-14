package fr.keyser.pt.effects;

import java.util.List;
import java.util.stream.Collectors;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.SpecialEffect;

public class PreserveFromDeathEffect implements SpecialEffect {

    @Override
    public void apply(DeployedCard card) {
	List<CardPosition> dyings = card.dyings().map(DeployedCard::getPosition).collect(Collectors.toList());
	if (!dyings.isEmpty())
	    card.addInputAction(new PreserveFromDeathSelector(dyings));
    }

}
