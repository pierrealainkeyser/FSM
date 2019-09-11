package fr.keyser.evolutions;

import java.util.List;

public class PlayerView extends AbstractPlayerView {

    private final List<CardView> inHands;

    private final List<OtherPlayerView> players;

    private final int foodPool;

    public PlayerView(PlayerSpeciesView species, PlayerStatus status, int foodPool, List<OtherPlayerView> players,
            List<CardView> inHands) {
	super(species, status);
	this.foodPool = foodPool;
	this.players = players;
	this.inHands = inHands;
    }

    public List<CardView> getInHands() {
	return inHands;
    }

    public List<OtherPlayerView> getPlayers() {
	return players;
    }

    public int getFoodPool() {
	return foodPool;
    }

}
