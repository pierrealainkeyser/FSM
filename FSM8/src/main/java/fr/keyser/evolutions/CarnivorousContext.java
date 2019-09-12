package fr.keyser.evolutions;

import java.util.List;

final class CarnivorousContext {

    private FeedingActionContext scavengers;

    private List<TargetAttackContext> targetAttackContexts;

    public CarnivorousContext(FeedingActionContext scavengers, List<TargetAttackContext> targetAttackContext) {
	this.scavengers = scavengers;
	this.targetAttackContexts = targetAttackContext;
    }

    public FeedingActionContext createContext() {
	return scavengers.duplicate();
    }

    public FeedingAttackOperation summary(Species carnivorous, Species target, AttackSummary summary) {
	SpeciesId uid = carnivorous.getUid();
	if (!summary.getAnalysis().isPossible())
	    return new FeedingAttackOperation(uid, summary, null);

	FeedingActionContext fac = createContext();

	OnAttackPopulationLoss populationLoss = summary.getPopulationLoss();

	fac.get(target)
	        .reduceCapacity(populationLoss.getVictim());

	SpeciesFeedingContext attacker = fac.get(carnivorous);

	PopulationLossSummary attackerLoss = populationLoss.getAttacker();
	if (attackerLoss != null && !populationLoss.getAttackerDamagePrevention().isPossible()) {
	    attacker.reduceCapacity(attackerLoss);
	}

	attacker.feedAttack(target);

	return new FeedingAttackOperation(uid, summary, fac.summary());

    }

    public List<TargetAttackContext> getTargetAttackContexts() {
	return targetAttackContexts;
    }
}