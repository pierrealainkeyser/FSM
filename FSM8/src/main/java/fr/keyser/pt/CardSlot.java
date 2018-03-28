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

    public void upgrade() {
	card.ifPresent(c -> c.setLevel(BuildingLevel.LEVEL2));
    }

    public boolean isCard(MetaCard meta) {
	return meta.equals(card.map(DeployedCard::getMeta).orElse(null));
    }

    public DeployedCard build(MetaCard building, BuildingLevel level) {
	CardModel model = new CardModel();
	model.setLevel(level);
	model.setMeta(building);

	withModel(model);
	return card.get();
    }

    public DeployedCard deploy(MetaCard unit, int turn) {
	CardModel model = new CardModel();
	model.setPlayedTurn(turn);
	model.setMeta(unit);

	withModel(model);
	return card.get();
    }

    void withModel(CardModel model) {
	card = Optional.of(new DeployedCard(player, position, model.getMeta(), model));
    }

    public DeployedCard redeploy(MetaCard unit) {
	DeployedCard dc = card.get();
	card = Optional.of(dc.withMeta(unit));
	return dc;
    }

    public void clear() {
	card = Optional.empty();
    }

    public boolean isPresent() {
	return card.isPresent();
    }

    public boolean isEmpty() {
	return !isPresent();
    }

    public Optional<DeployedCard> getCard() {
	return card;
    }

    public CardPosition getPosition() {
	return position;
    }

    @Override
    public String toString() {
	return "CardSlot [position=" + position + ", card=" + card + "]";
    }
}
