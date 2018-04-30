package fr.keyser.pt;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PlayerBoardContract {

    void processDraft(int id);

    void processDiscard(int id);

    void processDeployCardAction(DoDeployCard action);

    void keepToDeploy(int id);

    void processCardAction(CardAction action);

    void processBuild(int index);

    UUID getUUID();

    boolean hasInputActions();

    void deployPhase();

    void endOfDeployPhase();

    void buildPhase();

    void endBuildPhase();

    void agePhase();

    void endAgePhase();

    void goldPhase();

    Map<CardPosition, List<TargetedEffectDescription>> getInputActions();

    void refresh();
}