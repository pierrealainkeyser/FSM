package fr.keyser.pt;

import java.util.List;
import java.util.function.Predicate;

public final class ScopedSpecialEffect {
    private final SpecialEffectScope scope;
    private final SpecialEffect specialEffect;

    public ScopedSpecialEffect(SpecialEffectScope scope, SpecialEffect specialEffect) {
	this.scope = scope;
	this.specialEffect = specialEffect;
    }

    public boolean testTargeter(Predicate<TargetedSpecialEffect> pred) {
	if (isAsync())
	    return ((AsyncSpecialEffect) specialEffect).testTargeter(pred);
	return false;
    }

    public List<TargetedEffectDescription> asyncEffect(DeployedCard card) {
	if (isAsync())
	    return ((AsyncSpecialEffect) specialEffect).asyncEffect(card);
	return null;
    }

    public boolean isAsync() {
	return specialEffect instanceof AsyncSpecialEffect;
    }

    public SpecialEffectScope getScope() {
	return scope;
    }

    public SpecialEffect getSpecialEffect() {
	return specialEffect;
    }
}
