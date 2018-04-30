package fr.keyser.pt.event;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.PlayerBoard;

public abstract class DeployedCardEvent extends PlayerEvent {

    private final DeployedCard card;

    protected DeployedCardEvent(DeployedCard card, PlayerBoard board) {
	super(board);
	this.card = card;
    }

    public CardPosition getPosition() {
	return card.getPosition();
    }

    public DeployedCard getCard() {
	return card;
    }

}
