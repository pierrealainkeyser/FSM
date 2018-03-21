package fr.keyser.pt;

public class Card {

    private final IntValue combat;

    private final IntValue crystal;

    private final IntValue deployGold;

    private final IntValue deployLegend;

    private final IntValue dieGold;

    private final IntValue dieLegend;

    private final IntValue food;

    private final IntValue gold;

    private final BooleanValue mayCombat;

    private final IntValue warGold;

    private final IntValue warLegend;

    private final IntValue wood;

    Card(CardEssence<?> e) {
	this.food = e.food;
	this.wood = e.wood;
	this.crystal = e.crystal;
	this.combat = e.combat;
	this.gold = e.gold;
	this.warGold = e.warGold;
	this.warLegend = e.warLegend;
	this.dieGold = e.dieGold;
	this.dieLegend = e.dieLegend;
	this.mayCombat = e.mayCombat;
	this.deployGold = e.deployGold;
	this.deployLegend = e.deployLegend;
    }

    public final IntValue getCombat() {
	return combat;
    }

    public final IntValue getCrystal() {
	return crystal;
    }

    public final IntValue getDeployGold() {
	return deployGold;
    }

    public final IntValue getDeployLegend() {
	return deployLegend;
    }

    public final IntValue getDieGold() {
	return dieGold;
    }

    public final IntValue getDieLegend() {
	return dieLegend;
    }

    public final IntValue getFood() {
	return food;
    }

    public final IntValue getGold() {
	return gold;
    }

    public final BooleanValue getMayCombat() {
	return mayCombat;
    }

    public final String getName() {
	return getClass().getSimpleName();
    }

    public final IntValue getWarGold() {
	return warGold;
    }

    public final IntValue getWarLegend() {
	return warLegend;
    }

    public final IntValue getWood() {
	return wood;
    }
}
