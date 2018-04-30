package fr.keyser.pt.view;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import fr.keyser.bus.BroadcastingBus;
import fr.keyser.bus.PluggableBus;
import fr.keyser.pt.fsm.PlayerBoardFSM;

public class BoardViewsBuilderHub {

    private final PluggableBus bus;

    private final List<PlayerBoardFSM> players;

    public BoardViewsBuilderHub(PluggableBus bus, List<PlayerBoardFSM> players) {
	this.bus = bus;
	this.players = players;
    }

    private List<BoardViewUpdater> asUpdater() {
	return players.stream().map(BoardViewUpdater::new).collect(toList());
    }

    private Optional<PlayerBoardFSM> findMatching(UUID player) {
	return players.stream().filter(p -> p.getUuid().equals(player)).findFirst();
    }

    public List<BoardView> receive(UUID player, Object input) {
	Optional<PlayerBoardFSM> matching = findMatching(player);
	if (matching.isPresent()) {
	    try {
		List<BoardViewUpdater> delegateds = asUpdater();
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
