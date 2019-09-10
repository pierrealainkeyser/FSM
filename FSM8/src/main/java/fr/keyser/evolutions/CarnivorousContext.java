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

    public List<TargetAttackContext> getTargetAttackContexts() {
	return targetAttackContexts;
    }
}