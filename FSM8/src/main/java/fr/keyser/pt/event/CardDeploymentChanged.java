package fr.keyser.pt.event;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.PlayerBoard;

public class CardDeploymentChanged extends DeployedCardEvent {

    private final boolean deployed;

    public CardDeploymentChanged(DeployedCard card, PlayerBoard board, boolean deployed) {
	super(card, board);
	this.deployed = deployed;
    }

    public boolean isDeployed() {
	return deployed;
    }
}
