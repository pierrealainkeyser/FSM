package fr.keyser.evolutions;

public final class TraitAttackViolation extends AttackViolation {

    private final CardId cardId;

    public TraitAttackViolation(CardId cardId) {
	this.cardId = cardId;
    }

    public CardId getCardId() {
	return cardId;
    }
}