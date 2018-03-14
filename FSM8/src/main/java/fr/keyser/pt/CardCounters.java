package fr.keyser.pt;

public final class CardCounters extends Counters {

    private boolean mayCombat;

    public boolean isMayCombat() {
	return mayCombat;
    }

    public void setMayCombat(boolean mayCombat) {
	this.mayCombat = mayCombat;
    }
}
