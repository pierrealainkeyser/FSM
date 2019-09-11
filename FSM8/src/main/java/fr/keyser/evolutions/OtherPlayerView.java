package fr.keyser.evolutions;

public class OtherPlayerView extends AbstractPlayerView {

    private final int cardsInHand;

    public OtherPlayerView(PlayerSpeciesView species, PlayerStatus status, int cardsInHand) {
	super(species, status);
	this.cardsInHand = cardsInHand;

    }

    public int getCardsInHand() {
	return cardsInHand;
    }

}
