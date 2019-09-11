package fr.keyser.evolutions;

public final class Card {

    public final static Card UNKNOW = new Card(Trait.HIDDEN_TRAIT, 0);

    private final int food;

    private final Trait trait;

    public Card(Trait trait, int food) {
	this.trait = trait;
	this.food = food;
    }

    public CardView asView(CardId id, boolean faceDown) {
	return new CardView(id, food, trait, faceDown);
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