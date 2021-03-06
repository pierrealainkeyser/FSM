package fr.keyser.evolutions;

import java.util.List;

public final class PlayerView extends AbstractPlayerView {

    private final List<CardView> inHands;

    private final List<OtherPlayerView> players;

    private final int foodPool;

    private final GamePhase phase;

    private final int firstPlayer;

    private final List<FeedingOperation> operations;

    public PlayerView(int index, PlayerSpeciesView species, GamePhase phase, int firstPlayer, PlayerStatus status, int foodPool,
            List<OtherPlayerView> players, List<CardView> inHands, List<FeedingOperation> operations) {
	super(index, species, status);
	this.foodPool = foodPool;
	this.firstPlayer = firstPlayer;
	this.players = players;
	this.inHands = inHands;
	this.phase = phase;
	this.operations = operations;
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

    public GamePhase getPhase() {
	return phase;
    }

    public int getFirstPlayer() {
	return firstPlayer;
    }

    public List<FeedingOperation> getOperations() {
        return operations;
    }

}
