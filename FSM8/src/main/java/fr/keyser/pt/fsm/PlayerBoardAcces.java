package fr.keyser.pt.fsm;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.TargetedEffectDescription;

public interface PlayerBoardAcces {

    int getTurn();

    String getPhase();

    String getAppearance();

    UUID getUUID();

    Map<CardPosition, List<TargetedEffectDescription>> getInputActions();

    void receiveInput(Object input);

    void refresh();
}
