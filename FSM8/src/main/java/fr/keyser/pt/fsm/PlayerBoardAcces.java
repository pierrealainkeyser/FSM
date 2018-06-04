package fr.keyser.pt.fsm;

import java.util.UUID;

public interface PlayerBoardAcces {

    UUID getUUID();

    void receiveInput(Object input);

    void refresh();
}
