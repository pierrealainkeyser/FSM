package fr.keyser.evolutions;

public final class Card {

    public final static Card UNKNOW = new Card(CardId.UNKNOW, Trait.HIDDEN_TRAIT, 0);

    private final int food;

    private final CardId id;

    private final Trait trait;

    public Card(CardId id, Trait trait, int food) {
	this.id = id;
	this.trait = trait;
	this.food = food;
    }

    public int getFood() {
	return food;
    }

    public CardId getId() {
        return id;
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