package fr.keyser.evolutions;

public final class Card {

    private final int food;

    private final Trait trait;

    public Card(Trait trait, int food) {
	this.trait = trait;
	this.food = food;
    }

    public int getFood() {
	return food;
    }

    public Trait getTrait() {
	return trait;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Card[trait=").append(trait).append(", food=").append(food).append("]");
	return builder.toString();
    }
}