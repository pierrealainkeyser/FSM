package fr.keyser.pt;

public final class PlayerCounters extends Counters {

    private int victoriousWar;

    public PlayerCounters(PlayerCounters counters) {
	super(counters);
    }

    public PlayerCounters() {

    }

    public int getVictoriousWar() {
	return victoriousWar;
    }

    public void setVictoriousWar(int victoriousWar) {
	this.victoriousWar = victoriousWar;
    }
}
