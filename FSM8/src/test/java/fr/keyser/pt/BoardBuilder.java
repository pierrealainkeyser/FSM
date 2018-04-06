package fr.keyser.pt;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import fr.keyser.bus.Bus;
import fr.keyser.bus.Bus.Suscription;
import fr.keyser.pt.CardPosition.Position;

public class BoardBuilder {

    public class PlayerBuilder {
	private final List<DeployedCardInfo> cards = new ArrayList<>();

	private final PlayerBoardModel model = new PlayerBoardModel();

	private final Map<Position, Integer> positions = new EnumMap<>(Position.class);

	public PlayerBuilder toDeploy(MetaCard meta) {
	    model.getToDeploy().add(meta);
	    return this;
	}

	public PlayerBuilder addGold(int gold) {
	    model.addGold(gold);
	    return this;
	}

	public PlayerBuilder building(MetaCard meta, BuildingLevel level) {
	    cards.add(new DeployedCardInfo(position(Position.BUILDING), new CardModel(meta, level)));
	    return this;
	}

	public PlayerBuilder level1(MetaCard meta) {
	    return building(meta, BuildingLevel.LEVEL1);
	}

	public PlayerBuilder level2(MetaCard meta) {
	    return building(meta, BuildingLevel.LEVEL2);
	}

	public PlayerBuilder back(MetaCard meta) {
	    return front(meta, 0);
	}

	public PlayerBuilder back(MetaCard meta, int ageToken) {
	    return front(meta, ageToken, board.getTurnValue());
	}

	public PlayerBuilder back(MetaCard meta, int ageToken, int playedTurn) {
	    return register(meta, ageToken, playedTurn, Position.BACK);
	}

	public PlayerBoard build() {
	    PlayerBoard player = board.addPlayer(model, cards);
	    player.computeValues();
	    return player;
	}

	public PlayerBuilder front(MetaCard meta) {
	    return front(meta, 0);
	}

	public PlayerBuilder front(MetaCard meta, int ageToken) {
	    return front(meta, ageToken, board.getTurnValue());
	}

	public PlayerBuilder front(MetaCard meta, int ageToken, int playedTurn) {
	    return register(meta, ageToken, playedTurn, Position.FRONT);
	}

	private CardPosition position(Position p) {
	    Integer index = positions.merge(p, 0, (oldValue, newValue) -> (oldValue == null ? 0 : oldValue) + 1);
	    return p.index(index);
	}

	private PlayerBuilder register(MetaCard meta, int ageToken, int playedTurn, Position position) {
	    cards.add(new DeployedCardInfo(position(position), new CardModel(meta, ageToken, playedTurn)));
	    return this;
	}
    }

    private final Bus bus = new Bus();

    private final MetaCardBuilder cardBuilder = new MetaCardBuilder();

    private final MetaDeck deck = new MetaDeck();

    private final Board board = new Board(bus, deck);

    public PlayerBuilder player() {
	return new PlayerBuilder();
    }

    public MetaCard meta(Card card) {
	return cardBuilder.meta(card);
    }

    public Bus getBus() {
	return bus;
    }

    public <T> Suscription listenTo(Class<T> type, Consumer<? extends T> consumer) {
	return bus.listenTo(type, consumer);
    }

    public Board getBoard() {
        return board;
    }

}
