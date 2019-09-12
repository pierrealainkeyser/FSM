package fr.keyser.evolutions;

import java.util.List;

public final class TargetAttackContext {

    private final Species left;

    private final Species right;

    private final Species target;

    public TargetAttackContext(Species left, Species target, Species right) {
	this.left = left;
	this.target = target;
	this.right = right;
    }

    public TargetAttackAnalysis analyse(Species carnivorous) {
	return new TargetAttackAnalysis(carnivorous, target.getUid(), computeViolations(carnivorous));
    }

    private List<AttackViolation> computeViolations(Species carnivorous) {

	int population = target.getPopulation();

	AttackViolationBuilder builder = new AttackViolationBuilder();
	builder.trait(target, Trait.DEFENSIVE_HORDE, () -> carnivorous.getPopulation() < population);
	builder.trait(target, Trait.CLIMBER, () -> !carnivorous.hasTrait(Trait.CLIMBER));
	builder.trait(target, Trait.DIGGER, () -> target.getFoodLevel() >= population);
	builder.trait(left, Trait.ALARM, () -> !carnivorous.hasTrait(Trait.EMBUSCADE));
	builder.trait(right, Trait.ALARM, () -> !carnivorous.hasTrait(Trait.EMBUSCADE));
	builder.trait(target, Trait.SYMBIOSE, () -> target.getSize() < (right != null ? right.getSize() : 0));

	int size = carnivorous.getSize();
	if (carnivorous.hasTrait(Trait.PACK_HUNTER))
	    size += carnivorous.getPopulation();

	int attack = size;
	builder.trait(target, Trait.SHELL, () -> attack < population + 4);

	builder.size(attack, target);
	return builder.getViolations();
    }

    public boolean isSelf(Species other) {
	return target.isSame(other);
    }

    public final Species getTarget() {
        return target;
    }
}