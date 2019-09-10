package fr.keyser.evolutions;

public final class OnAttackPopulationLoss {

    private final PopulationLossSummary attacker;

    private final CostAnalysis attackerDamagePrevention;

    private final PopulationLossSummary victim;

    public OnAttackPopulationLoss(PopulationLossSummary attacker, CostAnalysis attackerDamagePrevention,
            PopulationLossSummary victim) {
        this.attackerDamagePrevention = attackerDamagePrevention;
        this.attacker = attacker;
        this.victim = victim;
    }

    public PopulationLossSummary getAttacker() {
        return attacker;
    }

    public CostAnalysis getAttackerDamagePrevention() {
        return attackerDamagePrevention;
    }

    public PopulationLossSummary getVictim() {
        return victim;
    }

}