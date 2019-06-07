package fr.keyser.pt2;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.keyser.pt2.prop.MutableInt;
import fr.keyser.pt2.units.Unit;

public class LocalGame {

    private final MutableInt turn = new MutableInt(0);

    private final Deck deck;

    private final List<LocalPlayer> players;

    private static <T> T next(List<T> input, int current, int direction) {
	int size = input.size();
	int directionMod = direction % size;
	int index = (size + current + directionMod) % size;
	return input.get(index);
    }

    public LocalGame(CardProvider cardProvider, LocalGameSettings settings) {
	this.deck = new Deck(cardProvider, settings.getDeck());
	int nbPlayers = settings.getNbPlayers();
	List<LocalPlayer> ps = new ArrayList<>(nbPlayers);
	for (int i = 0; i < nbPlayers; ++i) {
	    LocalBoard board = new LocalBoard(turn);
	    ps.add(new LocalPlayer(this, board));
	}
	this.players = Collections.unmodifiableList(ps);

	if (nbPlayers == 2) {
	    LocalPlayer before = players.get(0);
	    LocalPlayer after = players.get(1);

	    before.setNeighbour(after);
	    after.setNeighbour(before);

	} else {
	    for (int i = 0; i < nbPlayers; ++i) {
		LocalPlayer before = next(players, i, -1);
		LocalPlayer after = next(players, i, 1);
		LocalPlayer player = players.get(i);

		player.setNeighbours(before, after);
	    }
	}
    }

    public void distribute(int cards) {
	for (LocalPlayer lp : players) {
	    for (int i = 0; i < cards; ++i)
		lp.addToHand(deck.next());
	}
    }

    public void passCardsToNext() {
	boolean even = turn.getValue() % 2 == 0;
	int direction = even ? 1 : -1;

	List<List<String>> toDraft = players.stream().map(LocalPlayer::getCurrentDraft).collect(toList());

	for (int i = 0; i < players.size(); ++i) {
	    LocalPlayer p = players.get(i);
	    List<String> next = next(toDraft, i, direction);
	    p.setCurrentDraft(next);
	}
    }

    public void pickLastCard() {
	players.forEach(l -> l.pick(0));
    }

    public void nextTurn() {
	this.turn.add(1);
    }

    public boolean hasNextTurn() {
	return this.turn.getValue() < 4;
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

    Unit unit(String unitName) {
	return deck.unit(unitName);
    }

    void discard(String unitName) {
	deck.discard(unitName);
    }

    public List<LocalPlayer> getPlayers() {
	return players;
    }
}
