package fr.keyser.pt;

public final class PlayerCounters extends Counters {

    private int victoriousWar;

    public void sumDyingGain(CardCounters card) {
	dieGold += card.dieGold;
	dieLegend += card.dieLegend;
    }

    public void sumDeployGain(CardCounters card) {
	deployGold += card.deployGold;
	deployLegend += card.deployLegend;
    }

    public void sumGold(CardCounters card) {
	goldGain += card.goldGain;
    }

    public void resetBasicCounters() {
	goldGain = 2;
	combat = 0;
	crystal = 0;
	food = 0;
	wood = 0;
    }

    public void sumValues(CardCounters card) {
	if (card.isMayCombat())
	    combat += card.combat;
	crystal += card.crystal;
	food += card.food;
	wood += card.wood;
    }

    public void sumWarGain(CardCounters card) {
	warLegend += card.warLegend;
	warGold += card.warGold;
    }

    public int getVictoriousWar() {
	return victoriousWar;
    }

    public void setVictoriousWar(int victoriousWar) {
	this.victoriousWar = victoriousWar;
    }
}
