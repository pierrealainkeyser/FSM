package fr.keyser.pt;

public class RawBuildingCost {

    private int crystal;

    private int food;

    private int wood;

    private int gold;

    private RawBuildingCost alternate;

    public boolean isCovered() {
	return crystal == 0 && food == 0 && wood == 0 && gold == 0;
    }

    @Override
    public RawBuildingCost clone() {
	RawBuildingCost c = new RawBuildingCost();
	c.crystal = crystal;
	c.food = food;
	c.wood = wood;
	c.gold = gold;
	return c;
    }

    public RawBuildingCost required(RawBuildingCost other) {
	RawBuildingCost c = new RawBuildingCost();
	c.crystal = Math.max(0, crystal - other.crystal);
	c.food = Math.max(0, food - other.food);
	c.wood = Math.max(0, wood - other.wood);
	c.gold = Math.max(0, gold - other.gold);
	return c;
    }

    public RawBuildingCost add(RawBuildingCost other) {
	RawBuildingCost c = new RawBuildingCost();
	c.crystal = crystal + other.crystal;
	c.food = food + other.food;
	c.wood = wood + other.wood;
	c.gold = gold + other.gold;
	return c;
    }

    public int getCrystal() {
	return crystal;
    }

    public int getFood() {
	return food;
    }

    public int getWood() {
	return wood;
    }

    public int getGold() {
	return gold;
    }

    public RawBuildingCost crystal(int crystal) {
	this.crystal = crystal;
	return this;
    }

    public RawBuildingCost food(int food) {
	this.food = food;
	return this;
    }

    public RawBuildingCost wood(int wood) {
	this.wood = wood;
	return this;
    }

    public RawBuildingCost gold(int gold) {
	this.gold = gold;
	return this;
    }

    public RawBuildingCost alternate(RawBuildingCost alternate) {
	this.alternate = alternate;
	return this;
    }

    public RawBuildingCost getAlternate() {
	return alternate;
    }
}
