package fr.keyser.evolutions;

public final class TraitBasedFoodSource implements FoodSource {

    private final CardId cardId;

    private final FoodSource foodSource;

    public TraitBasedFoodSource(FoodSource foodSource, CardId cardId) {
	this.foodSource = foodSource;
	this.cardId = cardId;
    }

    @Override
    public int consume(int quantity) {
	return foodSource.consume(quantity);
    }

    public CardId getCardId() {
	return cardId;
    }

    @Override
    public FoodType getFoodType() {
	return foodSource.getFoodType();
    }

    @Override
    public FoodOrigin getOrigin() {
	return foodSource.getOrigin();
    }
}