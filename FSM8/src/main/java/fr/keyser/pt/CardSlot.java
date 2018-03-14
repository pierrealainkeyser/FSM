package fr.keyser.pt;

import java.util.Optional;

public final class CardSlot {
    private Optional<DeployedCard> card = Optional.empty();

    private final CardPosition position;

    private final PlayerBoard player;

    public CardSlot(PlayerBoard player, CardPosition position) {
	this.player = player;
	this.position = position;
    }

    public void build(MetaCard building, BuildingLevel level) {
	CardModel model = new CardModel();
	model.setLevel(level);
	model.setName(building.getCard().getName());

	card = Optional.of(new DeployedCard(player, position, building, model));
    }

    public void deploy(MetaCard unit, int turn) {
	CardModel model = new CardModel();
	model.setPlayedTurn(turn);
	model.setName(unit.getCard().getName());

	card = Optional.of(new DeployedCard(player, position, unit, model));
    }

    public void clear() {
	card = Optional.empty();
    }

    public Optional<DeployedCard> getCard() {
	return card;
    }

    public CardPosition getPosition() {
	return position;
    }
}
