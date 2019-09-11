package fr.keyser.evolutions;

public interface CardResolver {
    public Card resolve(CardId id);

    public default CardView asView(CardId id, boolean faceDown) {
	return resolve(id).asView(id, faceDown);
    }
}