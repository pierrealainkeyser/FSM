package fr.keyser.evolutions;

import java.util.List;

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

    public List<SpeciesView> getSpecies() {
	return species.getSpecies();
    }


}
