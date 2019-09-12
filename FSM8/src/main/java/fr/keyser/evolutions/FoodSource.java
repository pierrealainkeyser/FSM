package fr.keyser.evolutions;

public interface FoodSource {

    public int consume(int quantity);

    public FoodType getFoodType();

    public FoodOrigin getOrigin();

    public default FoodSource traitBased(CardId trait) {
	return new TraitBasedFoodSource(this, trait);
    }
}