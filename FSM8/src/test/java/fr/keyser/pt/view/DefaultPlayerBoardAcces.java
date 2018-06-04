package fr.keyser.pt.view;

import java.util.UUID;

import fr.keyser.pt.fsm.PlayerBoardAcces;

public class DefaultPlayerBoardAcces implements PlayerBoardAcces {

    private final UUID uuid;

    public DefaultPlayerBoardAcces() {
	this(UUID.randomUUID());
    }

    public DefaultPlayerBoardAcces(UUID uuid) {
	this.uuid = uuid;
    }

    @Override
    public UUID getUUID() {
	return uuid;
    }

    @Override
    public void receiveInput(Object input) {

    }

    @Override
    public void refresh() {

    }
}