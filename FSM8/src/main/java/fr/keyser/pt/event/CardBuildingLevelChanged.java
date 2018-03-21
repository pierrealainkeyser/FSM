package fr.keyser.pt.event;

import fr.keyser.pt.BuildingLevel;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.PlayerBoard;

public class CardBuildingLevelChanged extends DeployedCardEvent {

    private final BuildingLevel level;

    public CardBuildingLevelChanged(DeployedCard card, PlayerBoard board, BuildingLevel level) {
	super(card, board);
	this.level = level;
    }

    public BuildingLevel getLevel() {
	return level;
    }

}
