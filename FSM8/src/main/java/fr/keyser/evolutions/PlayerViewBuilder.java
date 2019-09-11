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

    public PlayerViewBuilder(Game game, List<Player> privates) {
	Map<PlayerId, PlayerView> views = new LinkedHashMap<>();

	List<PlayerDualView> duals = new ArrayList<>();

	int size = privates.size();
	for (int i = 0; i < size; ++i) {
	    Player publicPlayer = game.getPlayer(i);
	    Player privatePlayer = privates.get(i);
	    PlayerDualView dual = new PlayerDualView(privatePlayer.asView(publicPlayer, false),
	            privatePlayer.asView(publicPlayer, true));
	    duals.add(dual);
	}

	for (int i = 0; i < size; ++i) {
	    Player privatePlayer = privates.get(i);
	    PlayerSpeciesView currentView = duals.get(i).privateView;

	    List<OtherPlayerView> players = createPublic(privates, duals, i);

	    views.put(privatePlayer.getIndex(),
	            new PlayerView(privatePlayer.getIndex(), players, currentView, privatePlayer.handsAsView()));

	}

	this.views = Collections.unmodifiableMap(views);
    }

    private List<OtherPlayerView> createPublic(List<Player> privates, List<PlayerDualView> duals, int skip) {
	int size = duals.size();
	List<OtherPlayerView> privatesView = new ArrayList<>(size - 1);
	for (int j = 0; j < size; ++j) {
	    if (j != skip) {
		privatesView.add(new OtherPlayerView(privates.get(j).getHandSize(), duals.get(j).publicView));
	    }
	}
	return privatesView;
    }

    public Map<PlayerId, PlayerView> getViews() {
	return views;
    }

}
