package fr.keyser.evolutions;

import java.util.List;
import java.util.Map;

public final class TargetAttackAnalysis {

    private final CostAnalysis costs;

    private final Species target;

    private final List<AttackViolation> violations;

    private TargetAttackAnalysis(Species target, List<AttackViolation> violations, CostAnalysis costs) {
        this.target = target;
        this.violations = violations;
        this.costs = costs;
    }

    public TargetAttackAnalysis(Species carnivorous, Species target, List<AttackViolation> violations) {
        this(target, violations, CostAnalysis.analysis(carnivorous, violations));
    }

    public TargetAttackAnalysis filterCost(int max) {
        if (getTotalCost() <= max)
    	return this;

        return new TargetAttackAnalysis(target, violations, costs.tooCostly());
    }

    public Map<CardId, Integer> getCosts() {
        return costs.getCosts();
    }

    public Species getTarget() {
        return target;
    }

    public int getTotalCost() {
        return costs.getTotalCost();
    }

    public List<AttackViolation> getViolations() {
        return violations;
    }

    public boolean isPossible() {
        return costs.isPossible();
    }
}