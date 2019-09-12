package fr.keyser.evolutions;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class AttackSummary {
    @JsonUnwrapped
    private final TargetAttackAnalysis analysis;

    private final OnAttackPopulationLoss populationLoss;

    public AttackSummary(TargetAttackAnalysis analysis, OnAttackPopulationLoss populationLoss) {
	this.analysis = analysis;
	this.populationLoss = populationLoss;
    }

    public final TargetAttackAnalysis getAnalysis() {
	return analysis;
    }

    public final OnAttackPopulationLoss getPopulationLoss() {
	return populationLoss;
    }
}