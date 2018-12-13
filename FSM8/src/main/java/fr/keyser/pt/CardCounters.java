package fr.keyser.pt;

public final class CardCounters extends Counters {

    private boolean mayCombat;

    public CardCounters(CardCounters counters) {
	super(counters);
	this.mayCombat = counters.mayCombat;
    }

    public CardCounters() {

    }

    public boolean isMayCombat() {
	return mayCombat;
    }

    public void setMayCombat(boolean mayCombat) {
	this.mayCombat = mayCombat;
    }
}
