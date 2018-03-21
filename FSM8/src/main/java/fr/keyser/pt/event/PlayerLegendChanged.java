package fr.keyser.pt.event;

import fr.keyser.pt.PlayerBoard;

public class PlayerLegendChanged extends PlayerEvent {

    private final int legend;

    public PlayerLegendChanged(PlayerBoard board, int legend) {
	super(board);
	this.legend = legend;
    }

    public int getLegend() {
	return legend;
    }
}
