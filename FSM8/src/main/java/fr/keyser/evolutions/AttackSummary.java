package fr.keyser.evolutions;

public final class AttackSummary {
    private final TargetAttackAnalysis analysis;

    private final OnAttackPopulationLoss results;

    public AttackSummary(TargetAttackAnalysis analysis, OnAttackPopulationLoss results) {
	this.analysis = analysis;
	this.results = results;
    }

    public TargetAttackAnalysis getAnalysis() {
	return analysis;
    }

    public OnAttackPopulationLoss getPopulationLoss() {
	return results;
    }

    public boolean isPossible() {
	return results != null;
    }
}