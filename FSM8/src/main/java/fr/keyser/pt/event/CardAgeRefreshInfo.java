package fr.keyser.pt.event;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.PlayerBoard;

public class CardAgeRefreshInfo extends DeployedCardEvent {

    public CardAgeRefreshInfo(DeployedCard card, PlayerBoard board) {
	super(card, board);
    }
}
