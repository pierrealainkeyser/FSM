package fr.keyser.pt;

public class SpecialEffectScope {

    public enum When {
	AGING, DEPLOYEMENT, INITIAL_DEPLOYEMENT;

	public boolean match(SpecialEffectScope scope, DeployedCard card) {
	    boolean sameScope = scope.getWhen() == this;
	    if (AGING == this)
		return sameScope;
	    else if (DEPLOYEMENT == this)
		return sameScope || INITIAL_DEPLOYEMENT.match(scope, card);
	    else if (INITIAL_DEPLOYEMENT == this)
		return sameScope && card.isInitialDeploy();
	    else
		return false;
	}
    }

    private final int order;

    private final When when;

    public SpecialEffectScope(int order, When when) {
	this.order = order;
	this.when = when;
    }

    public int getOrder() {
	return order;
    }

    public When getWhen() {
	return when;
    }
}
