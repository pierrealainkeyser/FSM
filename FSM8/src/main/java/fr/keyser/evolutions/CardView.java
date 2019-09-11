package fr.keyser.evolutions;

public class CardView {

    private final boolean faceDown;

    private final int food;

    private CardId id;

    private final Trait trait;

    public CardView(CardId id, int food, Trait trait, boolean faceDown) {
	this.id = id;
	this.food = food;
	this.trait = trait;
	this.faceDown = faceDown;
    }

    public CardId getId() {
        return id;
    }

    public void setId(CardId id) {
        this.id = id;
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
