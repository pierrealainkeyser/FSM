package fr.keyser.pt;

import java.util.stream.Stream;

public interface BoardContract {

    void distributeCards();

    void passCardsToNext();

    void resetCounters();

    void newTurn();

    boolean isLastTurn();

    void deployPhaseEffect();

    void endOfDeployPhase();

    void warPhase();

    void goldPhase();

    void buildPhase();

    void agePhase();

    void endAgePhase();

    Stream<? extends PlayerBoardContract> getPlayers();

    void endBuildPhase();

}