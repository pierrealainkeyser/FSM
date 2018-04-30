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

    public final boolean isInitialDeploy() {
	return card.isInitialDeploy();
    }

    public final int getAgeToken() {
	return card.getAgeToken();
    }

    public final int getCombat() {
	return card.getCombat();
    }

    public final boolean isMayCombat() {
	return card.isMayCombat();
    }

    public final String getCardName() {
	return card.getMeta().getName();
    }

    public CardPosition getPosition() {
	return card.getPosition();
    }
}
