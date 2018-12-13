package fr.keyser.pt;

public class SpecialEffectScope {

    public enum When {
	AGING, DEPLOYEMENT, INITIAL_DEPLOYEMENT;
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

    public boolean match(When when) {
	return this.when == when;
    }
}
