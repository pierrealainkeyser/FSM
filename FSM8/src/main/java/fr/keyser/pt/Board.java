package fr.keyser.pt;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.bus.Bus;

public class Board implements BoardContract {

    private static <T> T next(List<T> input, int current, int direction) {
	int size = input.size();
	int directionMod = direction % size;
	return input.get((size + current + directionMod) % size);
    }

    private final MetaDeck deck;

    private final List<PlayerBoard> players = new ArrayList<>();

    private final Turn turn = new Turn();

    private Bus bus;

    public Board(MetaDeck deck) {
	this.deck = deck;
    }

    public MetaCard pickTopCard() {
	return deck.getCards().remove(0);
    }

    public PlayerBoard addNewPlayer() {
	PlayerBoardModel model = new PlayerBoardModel();
	model.setGold(3);

	PlayerBoard player = new PlayerBoard(UUID.randomUUID(), model, this);
	players.add(player);
	return player;
    }

    public void addPlayer(UUID uuid, PlayerBoardModel model) {
	players.add(new PlayerBoard(uuid, model, this));
    }

    @Override
    public void distributeCards() {
	List<MetaCard> cards = deck.getCards();

	int nb = 5;
	if (players.size() == 2)
	    nb = 9;

	for (PlayerBoard player : players) {
	    List<MetaCard> draft = new ArrayList<>();
	    for (int i = 0; i < nb; ++i)
		draft.add(cards.remove(0));

	    player.setToDraft(draft);
	}
    }

    @Override
    public Stream<? extends PlayerBoardContract> getPlayers() {
	return players.stream();
    }

    @Override
    public int getTurnValue() {
	return turn.getTurn();
    }

    @Override
    public boolean isLastTurn() {
	return turn.getTurn() == 4;
    }

    void moveToDiscard(MetaCard meta) {
	deck.getDiscarded().add(meta);
    }

    @Override
    public void newTurn() {
	turn.setTurn(turn.getTurn() + 1);
    }

    @Override
    public void passCardsToNext() {
	boolean even = turn.getTurn() % 2 == 0;
	int direction = even ? 1 : -1;

	List<List<MetaCard>> toDraft = players.stream().map(PlayerBoard::getToDraft).collect(Collectors.toList());

	for (int i = 0; i < players.size(); ++i)
	    players.get(i).setToDraft(next(toDraft, i, direction));
    }

    @Override
    public void resetCounters() {
	players.forEach(PlayerBoard::resetCounters);
    }

    boolean sameTurn(CardModel model) {
	return turn.getTurn() == model.getPlayedTurn();
    }

    @Override
    public void warPhase() {
	for (PlayerBoard player : players)
	    player.computeValues();

	for (int i = 0, size = players.size(); i < size; ++i) {
	    PlayerBoard p = players.get(i);
	    p.setVictoriousWar(warWinned(i));
	    p.computeWarGain();
	}
    }

    private int warWinned(int playerIndex) {

	int size = players.size();

	int current = players.get(playerIndex).getCombat();
	int right = next(players, playerIndex, +1).getCombat();
	int wins = 0;
	if (size > 2) {
	    int left = next(players, playerIndex, -1).getCombat();

	    if (current >= left)
		++wins;
	    if (current >= right)
		++wins;

	} else {
	    if (current >= 2 * right)
		wins = 2;
	    else if (current >= right)
		wins = 1;
	}
	return wins;
    }

    void forward(Object event) {
	bus.forward(event);
    }

    List<MetaCard> getBuildings() {
	return deck.getBuildings();
    }

}
