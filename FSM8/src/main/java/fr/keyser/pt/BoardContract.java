package fr.keyser.pt;

import java.util.stream.Stream;

public interface BoardContract {

    void distributeCards();

    void passCardsToNext();

    void resetCounters();

    void newTurn();

    boolean isLastTurn();

    void warPhase();

    Stream<? extends PlayerBoardContract> getPlayers();

    int getTurnValue();

}