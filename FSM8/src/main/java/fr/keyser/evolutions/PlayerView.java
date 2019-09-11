package fr.keyser.evolutions;

import java.util.List;

public class PlayerView {

    private final List<CardView> inHands;

    private final List<OtherPlayerView> players;

    private final PlayerId self;

    private final PlayerSpeciesView species;

    public PlayerView(PlayerId self, List<OtherPlayerView> players, PlayerSpeciesView species, List<CardView> inHands) {
	this.self = self;
	this.players = players;
	this.species = species;
	this.inHands = inHands;
    }

    public List<CardView> getInHands() {
	return inHands;
    }

    public List<OtherPlayerView> getPlayers() {
	return players;
    }

    public PlayerId getSelf() {
	return self;
    }

    public PlayerSpeciesView getSpecies() {
	return species;
    }

}
