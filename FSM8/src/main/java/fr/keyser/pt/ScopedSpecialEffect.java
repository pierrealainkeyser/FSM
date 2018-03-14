package fr.keyser.pt;

public final class ScopedSpecialEffect {
    private final SpecialEffectScope scope;
    private final SpecialEffect specialEffect;

    public ScopedSpecialEffect(SpecialEffectScope scope, SpecialEffect specialEffect) {
	this.scope = scope;
	this.specialEffect = specialEffect;
    }

    public SpecialEffectScope getScope() {
	return scope;
    }

    public SpecialEffect getSpecialEffect() {
	return specialEffect;
    }
}
