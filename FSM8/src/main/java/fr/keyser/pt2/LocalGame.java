package fr.keyser.pt2;

import java.util.ArrayList;
import java.util.List;

import fr.keyser.pt2.prop.MutableInt;

public class LocalGame {

    private final MutableInt turn = new MutableInt(0);

    private final Deck deck;

    private final List<LocalPlayer> players;

    public LocalGame(CardProvider cardProvider, LocalGameSettings settings) {
	this.deck = new Deck(cardProvider, settings.getDeck());
	int nbPlayers = settings.getNbPlayers();
	this.players = new ArrayList<>(nbPlayers);
	for (int i = 0; i < nbPlayers; ++i) {
	    LocalBoard board = new LocalBoard(turn);
	    players.add(new LocalPlayer(board));
	}

	if (nbPlayers == 2) {
	    LocalPlayer before = players.get(0);
	    LocalPlayer after = players.get(1);

	    before.setNeighbour(after);
	    after.setNeighbour(before);

	} else {
	    for (int i = 0; i < nbPlayers; ++i) {
		LocalPlayer before = players.get(((i + nbPlayers) - 1) % nbPlayers);
		LocalPlayer after = players.get(((i + nbPlayers) + 1) % nbPlayers);
		LocalPlayer player = players.get(i);

		player.setNeighbours(before, after);
	    }
	}
    }

    public void nextTurn() {
	this.turn.add(1);
    }

    public LocalPlayer getPlayer(int index) {
	return players.get(index);
    }

    public void agePhase() {
	players.forEach(LocalPlayer::agePhase);
    }

    public void endAgePhase() {
	players.forEach(LocalPlayer::endAgePhase);
    }

    public void deployPhase() {
	players.forEach(LocalPlayer::deployPhase);
    }

    public void endDeployPhase() {
	players.forEach(LocalPlayer::endDeployPhase);
    }

    public void payPhase() {
	players.forEach(LocalPlayer::payPhase);
    }

    public void warPhase() {
	players.forEach(LocalPlayer::warPhase);
    }

}
