package fr.keyser.pt;

import java.util.ArrayList;
import java.util.List;

public abstract class CardEssence<E extends CardEssence<E>> {

    IntValue dieLegend = IntValue.ZERO;

    IntValue warLegend = IntValue.ZERO;

    IntValue deployLegend = IntValue.ZERO;
    
    IntValue endOfGameLegend = IntValue.ZERO;

    IntValue food = IntValue.ZERO;

    IntValue wood = IntValue.ZERO;

    IntValue crystal = IntValue.ZERO;

    IntValue combat = IntValue.ZERO;

    IntValue gold = IntValue.ZERO;

    IntValue warGold = IntValue.ZERO;

    IntValue dieGold = IntValue.ZERO;

    IntValue deployGold = IntValue.ZERO;

    BooleanValue mayCombat = BooleanValue.FALSE;

    List<ScopedSpecialEffect> effects;

    CardEssence() {
    }

    public E effect(SpecialEffectScope scope, TargetedSpecialEffect targeter) {
	return effect(scope, new AsyncSpecialEffect(targeter));
    }

    public E effect(SpecialEffectScope scope, SpecialEffect specialEffect) {
	if (effects == null)
	    effects = new ArrayList<>();
	effects.add(new ScopedSpecialEffect(scope, specialEffect));
	return getThis();
    }

    public E food(IntValue food) {
	this.food = food;
	return getThis();
    }
    
    public E endOfGameLegend(IntValue endOfGameLegend) {
 	this.endOfGameLegend = endOfGameLegend;
 	return getThis();
     }

    public E warLegend(IntValue warLegend) {
	this.warLegend = warLegend;
	return getThis();
    }

    public E gold(IntValue gold) {
	this.gold = gold;
	return getThis();
    }

    public E warGold(IntValue warGold) {
	this.warGold = warGold;
	return getThis();
    }

    public E deployGold(IntValue deployGold) {
	this.deployGold = deployGold;
	return getThis();
    }

    public E dieGold(IntValue dieGold) {
	this.dieGold = dieGold;
	return getThis();
    }

    public E dieLegend(IntValue dieLegend) {
	this.dieLegend = dieLegend;
	return getThis();
    }

    public E deployLegend(IntValue deployLegend) {
	this.deployLegend = deployLegend;
	return getThis();
    }

    public E wood(IntValue wood) {
	this.wood = wood;
	return getThis();
    }

    public E crystal(IntValue crystal) {
	this.crystal = crystal;
	return getThis();
    }

    public E combat(IntValue combat) {
	this.combat = combat;
	return getThis();
    }

    public E mayCombat(BooleanValue mayCombat) {
	this.mayCombat = mayCombat;
	return getThis();
    }

    protected abstract E getThis();
}