package fr.keyser.evolutions;

public final class WateringHole implements FoodSource {

    private int consumed = 0;

    private final int food;

    public WateringHole(int food) {
	this.food = food;
    }

    @Override
    public int consume(int quantity) {
	int consume = Math.min(quantity, food - consumed);
	consumed += consume;
	return consume;
    }

    public int getConsumed() {
	return consumed;
    }

    @Override
    public FoodType getFoodType() {
	return FoodType.PLANT;
    }
}