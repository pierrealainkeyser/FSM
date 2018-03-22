package fr.keyser.pt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AsyncSpecialEffect implements SpecialEffect {

    private final TargetedSpecialEffect targeter;

    public AsyncSpecialEffect(TargetedSpecialEffect targeter) {
	this.targeter = targeter;
    }

    @Override
    public void apply(DeployedCard card) {
	Map<String, CardPosition> positions = new HashMap<>();
	for (TargetedEffectDescription ta : asyncEffect(card)) {

	    String name = ta.getName();
	    CardPosition position = card.positionFor(name);
	    positions.put(name, position);
	}

	targeter.apply(card, positions);

    }

    public List<TargetedEffectDescription> asyncEffect(DeployedCard card) {
	return targeter.asyncEffect(card);
    }

}
