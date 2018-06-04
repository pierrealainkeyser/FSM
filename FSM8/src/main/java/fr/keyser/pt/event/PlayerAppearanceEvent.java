package fr.keyser.pt.event;

import java.util.UUID;

public class PlayerAppearanceEvent extends PlayerEvent {

    private final String appearance;

    public PlayerAppearanceEvent(UUID player, String appearance) {
	super(player);
	this.appearance = appearance;
    }

    public String getAppearance() {
	return appearance;
    }

}
