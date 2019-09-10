package fr.keyser.evolutions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CostAnalysis {

    public static CostAnalysis analysis(Species carnivorous, List<AttackViolation> violations) {
	if (violations.isEmpty()) {
	    return CostAnalysis.ok();
	} else {
	    Optional<CardId> intelligentCard = carnivorous.findCardWithTrait(Trait.INTELIGGENT);
	    boolean intelligent = intelligentCard.isPresent();
	    int count = (int) violations.stream().filter(a -> intelligent && a instanceof TraitAttackViolation).count();

	    if (violations.size() == count) {
		return CostAnalysis.conditional(Collections.singletonMap(intelligentCard.get(), count));
	    } else {
		return CostAnalysis.imposible();
	    }
	}
    }

    public static CostAnalysis conditional(Map<CardId, Integer> costs) {
	return new CostAnalysis(true, costs);
    }

    public static CostAnalysis imposible() {
	return new CostAnalysis(false, Collections.emptyMap());
    }

    public static CostAnalysis ok() {
	return new CostAnalysis(true, Collections.emptyMap());
    }

    private final Map<CardId, Integer> costs;

    private final boolean possible;

    private CostAnalysis(boolean possible, Map<CardId, Integer> costs) {
	this.possible = possible;
	this.costs = costs;
    }

    public Map<CardId, Integer> getCosts() {
	return costs;
    }

    public int getTotalCost() {
	return getCosts().values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isPossible() {
	return possible;
    }

    public CostAnalysis tooCostly() {
	return new CostAnalysis(false, costs);
    }

}