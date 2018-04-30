package fr.keyser.pt.event;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.PlayerBoard;

public class CardRefreshInfo extends DeployedCardEvent {

    public CardRefreshInfo(DeployedCard card, PlayerBoard board) {
	super(card, board);
    }
}
