package fr.keyser.pt.view;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.TargetedEffectDescription;
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
    public String getAppearance() {

        return null;
    }

    @Override
    public Map<CardPosition, List<TargetedEffectDescription>> getInputActions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void receiveInput(Object input) {

    }
}