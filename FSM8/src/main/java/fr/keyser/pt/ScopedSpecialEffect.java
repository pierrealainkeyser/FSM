package fr.keyser.pt;

import java.util.stream.Stream;

import fr.keyser.pt.SpecialEffectScope.When;
import fr.keyser.pt.effects.ChoosenTargets;
import fr.keyser.pt.effects.Target;
import fr.keyser.pt.effects.TargetableEffect;

public final class ScopedSpecialEffect {
    private final SpecialEffectScope scope;
    private final TargetableEffect effect;
    private final int index;

    public ScopedSpecialEffect(SpecialEffectScope scope, int index, TargetableEffect effect) {
	this.scope = scope;
	this.index = index;
	this.effect = effect;
    }

    public String getName() {
	return "TODO";
    }

    public Stream<Target> targets(DeployedCard source) {
	return effect.targets(source);
    }

    public void apply(DeployedCard source, ChoosenTargets targets) {
	effect.apply(source, targets);
    }

    public boolean match(When when) {
	return scope.match(when);
    }

    public int getOrder() {
	return scope.getOrder();
    }

    public int getIndex() {
	return index;
    }
}
