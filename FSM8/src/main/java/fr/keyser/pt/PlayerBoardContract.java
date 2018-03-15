package fr.keyser.pt;

public interface PlayerBoardContract {

    void processDraft(int id);

    void processDiscard(int id);

    void processDeployCardAction(DoDeployCard action);

    void keepToDeploy(int id);

    void processCardAction(CardAction action);

}