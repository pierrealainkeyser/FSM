package fr.keyser.evolutions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

final class AttackViolationBuilder {
    private List<AttackViolation> violations = new ArrayList<>();

    public List<AttackViolation> getViolations() {
	return new ArrayList<>(violations);
    }

    public void size(int attack, Species target) {

	if (attack < target.getSize())
	    violations.add(new SimpleAttackViolation());
    }

    private void trait(Optional<Species> species, Trait trait, Supplier<Boolean> additionalCheck) {
	Optional<CardId> card = species.flatMap(s -> s.findCardWithTrait(trait));
	if (card.isPresent() && additionalCheck.get()) {
	    violations.add(new TraitAttackViolation(card.get()));
	}
    }

    public void trait(Species species, Trait trait, Supplier<Boolean> additionalCheck) {
	trait(Optional.ofNullable(species), trait, additionalCheck);
    }
}