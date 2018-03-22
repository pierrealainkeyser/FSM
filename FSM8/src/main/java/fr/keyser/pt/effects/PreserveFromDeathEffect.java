package fr.keyser.pt.effects;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.TargetedEffectDescription;
import fr.keyser.pt.TargetedSpecialEffect;

public class PreserveFromDeathEffect implements TargetedSpecialEffect {

    @Override
    public List<TargetedEffectDescription> asyncEffect(DeployedCard source) {
	List<CardPosition> dyings = source.dyings().map(DeployedCard::getPosition).collect(Collectors.toList());
	if (dyings.isEmpty())
	    return null;
	return Arrays.asList(new TargetedEffectDescription("save", dyings));
    }

    @Override
    public void apply(DeployedCard source, Map<String, CardPosition> positions) {
	CardPosition saved = positions.get("save");

	source.preserveFromDeath(saved);

    }

}
