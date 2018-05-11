package fr.keyser.pt.event;

import java.util.UUID;

import fr.keyser.pt.PlayerBoard;

public abstract class PlayerEvent {

    private final UUID player;

    public PlayerEvent(UUID player) {
	this.player = player;
    }

    public PlayerEvent(PlayerBoard board) {
	this.player = board.getUUID();
    }

    public UUID getPlayer() {
	return player;
    }

}