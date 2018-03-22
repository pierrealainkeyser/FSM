package fr.keyser.pt.effects;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.TargetedEffectDescription;
import fr.keyser.pt.TargetedSpecialEffect;

public class DropAgeTokenEffect implements TargetedSpecialEffect {

    private final IntValue ageCount;

    public DropAgeTokenEffect(IntValue ageCount) {
	this.ageCount = ageCount;
    }

    @Override
    public List<TargetedEffectDescription> asyncEffect(DeployedCard source) {
	int age = ageCount.getValue(source);
	if (age > 0) {
	    return Arrays.asList(new TargetedEffectDescription("drop", null));
	}
	return null;
    }

    @Override
    public void apply(DeployedCard source, Map<String, CardPosition> positions) {
	CardPosition to = positions.get("drop");
	int age = ageCount.getValue(source);
	source.find(to).doAge(age);

    }

}
