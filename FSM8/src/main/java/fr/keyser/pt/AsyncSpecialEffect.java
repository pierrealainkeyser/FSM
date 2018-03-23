package fr.keyser.pt;

import java.util.List;

public final class AsyncSpecialEffect implements SpecialEffect {

    private final TargetedSpecialEffect targeter;

    public AsyncSpecialEffect(TargetedSpecialEffect targeter) {
	this.targeter = targeter;
    }

    @Override
    public void apply(DeployedCard card) {
	apply(card, card);
    }

    public void apply(DeployedCard target, DeployedCard sourceArgs) {
	targeter.apply(target, sourceArgs.getSelectedAndClear());
    }

    public List<TargetedEffectDescription> asyncEffect(DeployedCard card) {
	return targeter.asyncEffect(card);
    }

}
