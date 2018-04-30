package fr.keyser.pt.view;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import fr.keyser.bus.BroadcastingBus;
import fr.keyser.bus.PluggableBus;
import fr.keyser.pt.fsm.PlayerBoardAcces;

public class BoardViewsBuilderHub {

    private final PluggableBus bus;

    private final List<PlayerBoardAcces> players;

    public BoardViewsBuilderHub(PluggableBus bus, List<PlayerBoardAcces> players) {
	this.bus = bus;
	this.players = players;
    }

    private List<BoardViewUpdater> updaters() {
	return players.stream().map(BoardViewUpdater::new).collect(toList());
    }

    private Optional<PlayerBoardAcces> findMatching(UUID player) {
	return players.stream().filter(p -> p.getUUID().equals(player)).findFirst();
    }

    public BoardView refresh(UUID player) {
	Optional<PlayerBoardAcces> matching = findMatching(player);
	if (matching.isPresent()) {
	    try {
		BoardViewUpdater delegated = new BoardViewUpdater(matching.get());
		bus.setDelegated(delegated);

		matching.get().refresh();

		return delegated.getView();
	    } finally {
		bus.setDelegated(null);
	    }
	} else
	    return null;
    }

    public List<BoardView> receive(UUID player, Object input) {
	Optional<PlayerBoardAcces> matching = findMatching(player);
	if (matching.isPresent()) {
	    try {
		List<BoardViewUpdater> delegateds = updaters();
		bus.setDelegated(new BroadcastingBus(delegateds));

		matching.get().receiveInput(input);

		return delegateds.stream().map(BoardViewUpdater::getView).collect(toList());
	    } finally {
		bus.setDelegated(null);
	    }
	} else
	    return null;
    }
}
