package fr.keyser.pt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Unit extends Card {

    public static UnitEssence essence(int goldCost) {
	return new UnitEssence(goldCost);
    }

    final int goldCost;

    final BooleanValue deathCondition;

    final List<ScopedSpecialEffect> effects;

    protected Unit(UnitEssence e) {
	super(e);
	this.goldCost = e.goldCost;
	this.deathCondition = e.deathCondition;
	this.effects = e.effects == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(e.effects));
    }

    public final int getGoldCost() {
	return goldCost;
    }

    public final BooleanValue getDeathCondition() {
	return deathCondition;
    }

    public final List<ScopedSpecialEffect> getEffects() {
	return effects;
    }
}
