package fr.keyser.pt;

import java.util.List;
import java.util.Map;

public final class AsyncSpecialEffect implements SpecialEffect {

    private final TargetedSpecialEffect targeter;

    public AsyncSpecialEffect(TargetedSpecialEffect targeter) {
	this.targeter = targeter;
    }

    @Override
    public void apply(DeployedCard card) {
	apply(card, card.getSelectedAndClear());
    }

    public void apply(DeployedCard target, Map<String, CardPosition> positions) {
	targeter.apply(target, positions);
    }

    public List<TargetedEffectDescription> asyncEffect(DeployedCard card) {
	return targeter.asyncEffect(card);
    }

}
