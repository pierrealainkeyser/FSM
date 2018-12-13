package fr.keyser.pt;

import java.util.Optional;

import fr.keyser.pt.effects.ChoosenTargets;

public final class FiredEffect {
    private int index;

    private String name;

    private int order;

    private CardPosition position;

    private ChoosenTargets targets;

    public FiredEffect() {

    }

    public FiredEffect(DeployedCard card, ScopedSpecialEffect effect) {
	this.position = card.getPosition();
	this.index = effect.getIndex();
	this.order = effect.getOrder();
	this.name = effect.getName();
    }

    public boolean match(CardAction action) {
	return position.equals(action.getSource()) && index == action.getIndex();
    }

    public void use(CardAction action) {
	targets = new ChoosenTargets(action.getTarget());
    }

    public void fire(PlayerBoard board) {
	Optional<DeployedCard> card = board.find(position).getCard();
	if (card.isPresent()) {
	    DeployedCard source = card.get();
	    ScopedSpecialEffect effect = source.effects(index);
	    effect.apply(source, targets);
	}
    }

    public int getIndex() {
	return index;
    }

    public String getName() {
	return name;
    }

    public int getOrder() {
	return order;
    }

    public CardPosition getPosition() {
	return position;
    }

}