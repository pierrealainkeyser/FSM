package fr.keyser.pt.effects;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.SpecialEffect;

public class DropAgeTokenEffect implements SpecialEffect {

    private final IntValue ageCount;

    public DropAgeTokenEffect(IntValue ageCount) {
	this.ageCount = ageCount;
    }

    @Override
    public void apply(DeployedCard card) {
	int age = ageCount.getValue(card);
	if (age > 0)
	    card.addInputAction(new DropAgeTokenSelector(age));
    }

}
