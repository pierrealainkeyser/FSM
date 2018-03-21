package fr.keyser.pt.event;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.MetaCard;
import fr.keyser.pt.PlayerBoard;

abstract class DeployedCardEvent extends PlayerEvent {
    private final CardPosition position;

    private final MetaCard meta;

    protected DeployedCardEvent(DeployedCard card, PlayerBoard board) {
	super(board);
	position = card.getPosition();
	meta = card.getMeta();

    }

    public CardPosition getPosition() {
	return position;
    }

    public MetaCard getMeta() {
	return meta;
    }
}
