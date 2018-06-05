package fr.keyser.pt;

import java.util.stream.Stream;

import fr.keyser.bus.Bus;

public interface BoardContract extends Bus {

    int getTurn();

    void distributeCards();

    void passCardsToNext();

    void acquireLastDraft();

    void resetCounters();

    void newTurn();

    boolean isLastTurn();

    void warPhase();

    Stream<PlayerBoardContract> getPlayers();
}