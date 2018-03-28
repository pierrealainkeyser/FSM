package fr.keyser.pt;

public final class UnitEssence extends CardEssence<UnitEssence> {

    private final static BooleanValue DEFAULT_COMBAT = BooleanValue.card(DeployedCard::isOnFrontLine);

    private final static BooleanValue DEFAULT_DEATH = BooleanValue.card(DeployedCard.hasALeastAgeToken(1));

    final int goldCost;

    BooleanValue deathCondition = DEFAULT_DEATH;

    UnitEssence(int goldCost) {
	this.goldCost = goldCost;
	mayCombat(DEFAULT_COMBAT);
	combat(IntValue.ONE);
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
