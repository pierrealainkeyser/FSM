package fr.keyser.evolutions;

public final class OtherPlayerView extends AbstractPlayerView {

    private final int cardsInHand;

    public OtherPlayerView(int index, PlayerSpeciesView species, PlayerStatus status, int cardsInHand) {
	super(index, species, status);
	this.cardsInHand = cardsInHand;

    }

    public int getCardsInHand() {
	return cardsInHand;
    }

}
