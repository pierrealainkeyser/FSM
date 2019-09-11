package fr.keyser.evolutions;

public class OtherPlayerView {

    private final int cardsInHand;

    private final PlayerSpeciesView species;

    public OtherPlayerView(int cardsInHand, PlayerSpeciesView species) {
	this.cardsInHand = cardsInHand;
	this.species = species;
    }

    public int getCardsInHand() {
        return cardsInHand;
    }

    public PlayerSpeciesView getSpecies() {
        return species;
    }

}
