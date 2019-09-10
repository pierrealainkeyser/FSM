package fr.keyser.evolutions;

public final class UnlimitedFoodSource implements FoodSource {
    public static final UnlimitedFoodSource MEAT = new UnlimitedFoodSource(FoodType.MEAT);

    public static final UnlimitedFoodSource PLANT = new UnlimitedFoodSource(FoodType.PLANT);

    private final FoodType foodType;

    private UnlimitedFoodSource(FoodType foodType) {
	this.foodType = foodType;
    }

    @Override
    public int consume(int quantity) {
	return quantity;
    }

    @Override
    public FoodType getFoodType() {
	return foodType;
    }

}