package fr.keyser.evolutions;

public class CardView {

    private final boolean faceDown;

    private final int food;

    private CardId card;

    private final Trait trait;

    public CardView(CardId card, int food, Trait trait, boolean faceDown) {
	this.card = card;
	this.food = food;
	this.trait = trait;
	this.faceDown = faceDown;
    }

    public CardId getCard() {
        return card;
    }

    public void setCard(CardId id) {
        this.card = id;
    }

    public boolean isFaceDown() {
        return faceDown;
    }

    public int getFood() {
        return food;
    }

    public Trait getTrait() {
        return trait;
    }

}
