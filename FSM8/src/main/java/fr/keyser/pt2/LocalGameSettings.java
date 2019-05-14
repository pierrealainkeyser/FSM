package fr.keyser.pt2;

public class LocalGameSettings {

    private final DeckMemento deck;

    private final int nbPlayers;

    public LocalGameSettings(DeckMemento deck, int nbPlayers) {
	this.deck = deck;
	this.nbPlayers = nbPlayers;
    }

    public DeckMemento getDeck() {
	return deck;
    }

    public int getNbPlayers() {
	return nbPlayers;
    }

}
