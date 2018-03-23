package fr.keyser.pt;

import java.util.ArrayList;
import java.util.List;

public final class UnitEssence extends CardEssence<UnitEssence> {

    private final static BooleanValue DEFAULT_COMBAT = BooleanValue.card(DeployedCard::isOnFrontLine);

    private final static BooleanValue DEFAULT_DEATH = BooleanValue.card(DeployedCard.hasALeastAgeToken(1));

    final int goldCost;

    BooleanValue deathCondition = DEFAULT_DEATH;

    List<ScopedSpecialEffect> effects;

    UnitEssence(int goldCost) {
	this.goldCost = goldCost;
	mayCombat(DEFAULT_COMBAT);
	combat(IntValue.ONE);
    }

    public UnitEssence effect(SpecialEffectScope scope, TargetedSpecialEffect targeter) {
	return effect(scope, new AsyncSpecialEffect(targeter));
    }

    public UnitEssence effect(SpecialEffectScope scope, SpecialEffect specialEffect) {
	if (effects == null)
	    effects = new ArrayList<>();
	effects.add(new ScopedSpecialEffect(scope, specialEffect));
	return getThis();
    }

    public UnitEssence deathCondition(BooleanValue deathCondition) {
	this.deathCondition = deathCondition;
	return getThis();
    }

    @Override
    protected UnitEssence getThis() {
	return this;
    }
}
