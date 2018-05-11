package fr.keyser.pt.event;

import java.util.UUID;

public class PlayerIdleEvent extends PlayerEvent {

    private final boolean idle;

    public PlayerIdleEvent(UUID player, boolean idle) {
	super(player);
	this.idle = idle;
    }

    public boolean isIdle() {
	return idle;
    }
}
