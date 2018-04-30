package fr.keyser.pt.event;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.PlayerBoard;

public class CardDeploymentChanged extends DeployedCardEvent {

    private final boolean deployed;

    private final DeployedCard newCard;

    public CardDeploymentChanged(DeployedCard card, PlayerBoard board, boolean deployed, DeployedCard newCard) {
	super(card, board);
	this.deployed = deployed;
	this.newCard = newCard;
    }

    public boolean isDeployed() {
	return deployed;
    }

    public DeployedCard getNewCard() {
        return newCard;
    }
}
