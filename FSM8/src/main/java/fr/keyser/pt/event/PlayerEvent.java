package fr.keyser.pt.event;

import java.util.UUID;

import fr.keyser.pt.PlayerBoard;

abstract class PlayerEvent {

    private final UUID player;

    public PlayerEvent(PlayerBoard board) {
	this.player = board.getUuid();
    }

    public UUID getPlayer() {
	return player;
    }

}