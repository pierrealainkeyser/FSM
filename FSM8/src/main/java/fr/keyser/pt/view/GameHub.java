package fr.keyser.pt.view;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.keyser.bus.PluggableBus;
import fr.keyser.fsm.SequentialExecutor;
import fr.keyser.pt.fsm.PlayerBoardAcces;

public class GameHub {

    private final SequentialExecutor executor = new SequentialExecutor();

    private final Map<UUID, GameEndpoint> endpoints = new HashMap<>();

    private final BoardViewsBuilderHub hub;

    public GameHub(PluggableBus bus, List<? extends PlayerBoardAcces> players) {
	this(new BoardViewsBuilderHub(bus, players));
    }

    public GameHub(BoardViewsBuilderHub hub) {
	this.hub = hub;
    }

    public void register(UUID player, GameEndpoint endpoint) {
	executor.execute(() -> {
	    BoardView view = hub.refresh(player);
	    if (view != null) {
		endpoints.put(player, endpoint);
		send(asList(view));
	    }
	});
    }

    public void receive(UUID player, Object input) {
	executor.execute(() -> {
	    List<BoardView> views = hub.receive(player, input);
	    if (views != null)
		send(views);
	});
    }

    private void send(List<BoardView> views) {
	List<UUID> removeds = new ArrayList<>();

	for (BoardView view : views) {
	    UUID local = view.getLocal();

	    GameEndpoint endpoint = endpoints.get(local);
	    if (endpoint != null) {
		try {
		    endpoint.send(view);
		} catch (Exception e) {
		    endpoints.remove(local);
		    removeds.add(local);
		}
	    }
	}

	// FIXME update removed state
    }
}
