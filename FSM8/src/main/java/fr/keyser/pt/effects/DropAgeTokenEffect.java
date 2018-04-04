package fr.keyser.pt.effects;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.TargetedEffectDescription;
import fr.keyser.pt.TargetedSpecialEffect;

public class DropAgeTokenEffect implements TargetedSpecialEffect {

    public static final String DROP_AGE = "dropAge";
    private final IntValue ageCount;

    public DropAgeTokenEffect(IntValue ageCount) {
	this.ageCount = ageCount;
    }

    @Override
    public List<TargetedEffectDescription> asyncEffect(DeployedCard source) {
	int age = ageCount.getValue(source);
	return asList(new IntTargetedEffectDescription(DROP_AGE, age, source.getPlayer().units()));
    }

    @Override
    public void apply(DeployedCard source, Map<String, CardPosition> positions) {
	CardPosition to = positions.get(DROP_AGE);
	if (to == null)
	    throw new IllegalStateException(DROP_AGE + " position's not registered");
	int age = ageCount.getValue(source);
	source.find(to).doAge(age);

    }

}
