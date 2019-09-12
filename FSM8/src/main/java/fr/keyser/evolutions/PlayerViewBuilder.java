package fr.keyser.evolutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlayerViewBuilder {

    private static class PlayerDualView {
	private final PlayerSpeciesView privateView;

	private final PlayerSpeciesView publicView;

	public PlayerDualView(PlayerSpeciesView privateView, PlayerSpeciesView publicView) {
	    this.privateView = privateView;
	    this.publicView = publicView;
	}
    }

    private final Map<PlayerId, PlayerView> views;

    public PlayerViewBuilder(GamePhase phase, Game game, List<PlayerAndStatus> privates) {
	Map<PlayerId, PlayerView> views = new LinkedHashMap<>();

	List<PlayerDualView> duals = new ArrayList<>();

	int size = privates.size();
	for (int i = 0; i < size; ++i) {
	    Player publicPlayer = game.getPlayer(i);
	    Player privatePlayer = privates.get(i).getPlayer();
	    PlayerDualView dual = new PlayerDualView(privatePlayer.asView(publicPlayer, false),
	            privatePlayer.asView(publicPlayer, true));
	    duals.add(dual);
	}

	for (int i = 0; i < size; ++i) {
	    PlayerAndStatus pas = privates.get(i);
	    Player privatePlayer = pas.getPlayer();
	    PlayerSpeciesView currentView = duals.get(i).privateView;

	    List<OtherPlayerView> players = createPublic(privates, duals, i);

	    views.put(privatePlayer.getIndex(),
	            new PlayerView(i, currentView, phase, game.getFirstPlayer(), pas.getStatus(), game.getFoodPool(), players,
	                    privatePlayer.handsAsView()));

	}

	this.views = Collections.unmodifiableMap(views);
    }

    private List<OtherPlayerView> createPublic(List<PlayerAndStatus> privates, List<PlayerDualView> duals, int skip) {
	int size = duals.size();
	List<OtherPlayerView> privatesView = new ArrayList<>(size - 1);
	for (int j = 0; j < size; ++j) {
	    if (j != skip) {
		PlayerAndStatus pas = privates.get(j);
		privatesView.add(new OtherPlayerView(j, duals.get(j).publicView, pas.getStatus(), pas.getPlayer().getHandSize()));
	    }
	}
	return privatesView;
    }

    public Map<PlayerId, PlayerView> getViews() {
	return views;
    }

}
