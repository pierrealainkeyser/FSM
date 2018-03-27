package fr.keyser.pt;

import java.util.UUID;

public interface PlayerBoardContract {

    void processDraft(int id);

    void processDiscard(int id);

    void processDeployCardAction(DoDeployCard action);

    void keepToDeploy(int id);

    void processCardAction(CardAction action);

    void doBuild(int index);

    UUID getUuid();

    boolean hasInputActions();

    void deployPhaseEffect();

    void endOfDeployPhase();

    void buildPhase();

    void endBuildPhase();

    void agePhase();

    void endAgePhase();

    void goldPhase();
}