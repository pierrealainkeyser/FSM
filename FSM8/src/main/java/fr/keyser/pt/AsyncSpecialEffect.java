package fr.keyser.pt;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class AsyncSpecialEffect implements SpecialEffect {

    private final TargetedSpecialEffect targeter;

    public AsyncSpecialEffect(TargetedSpecialEffect targeter) {
	this.targeter = targeter;
    }

    public boolean testTargeter(Predicate<TargetedSpecialEffect> pred) {
	return pred.test(targeter);
    }

    @Override
    public void apply(DeployedCard card) {
	apply(card, card.getSelected());
    }

    public void apply(DeployedCard target, Map<String, CardPosition> positions) {
	targeter.apply(target, positions);
    }

    public List<TargetedEffectDescription> asyncEffect(DeployedCard card) {
	return targeter.asyncEffect(card);
    }

}
