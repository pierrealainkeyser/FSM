package fr.keyser.pt;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.bus.Bus;
import fr.keyser.bus.NoopBus;

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
	this(new NoopBus(), deck);
    }

    public Board(Bus bus, MetaDeck deck) {
	this.bus = bus;
	this.deck = deck;
    }

    void refreshAll() {
	players.forEach(PlayerBoard::doRefresh);
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

    public PlayerBoard addPlayer(UUID uuid, PlayerBoardModel model) {
	PlayerBoard player = new PlayerBoard(uuid, model, this);
	players.add(player);
	return player;
    }

    PlayerBoard addPlayer(PlayerBoardModel model) {
	return addPlayer(model, null);
    }

    PlayerBoard addPlayer(PlayerBoardModel model, List<DeployedCardInfo> cards) {
	PlayerBoard player = addPlayer(UUID.randomUUID(), model);
	if (cards != null) {
	    for (DeployedCardInfo dci : cards)
		player.find(dci.getPosition()).withModel(dci.getModel());
	}
	return player;
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
    public Stream<PlayerBoardContract> getPlayers() {
	return players.stream().map(p -> (PlayerBoardContract) p);
    }

    int getTurnValue() {
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

    @Override
    public void forward(Object event) {
	bus.forward(event);
    }

    List<MetaCard> getBuildings() {
	return deck.getBuildings();
    }

    @Override
    public int getTurn() {
	return turn.getTurn();
    }

}
