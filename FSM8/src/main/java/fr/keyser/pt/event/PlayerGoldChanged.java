package fr.keyser.pt.event;

import fr.keyser.pt.PlayerBoard;

public class PlayerGoldChanged extends PlayerEvent {

    private final int gold;

    public PlayerGoldChanged(PlayerBoard board, int gold) {
	super(board);
	this.gold = gold;
    }

    public int getGold() {
	return gold;
    }
}
