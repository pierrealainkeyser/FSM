package fr.keyser.pt;

public abstract class Unit extends Card {

    public static UnitEssence essence(int goldCost) {
	return new UnitEssence(goldCost);
    }

    final int goldCost;

    final BooleanValue deathCondition;

    protected Unit(UnitEssence e) {
	super(e);
	this.goldCost = e.goldCost;
	this.deathCondition = e.deathCondition;

    }

    public final int getGoldCost() {
	return goldCost;
    }

    public final BooleanValue getDeathCondition() {
	return deathCondition;
    }

}
