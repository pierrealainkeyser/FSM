package fr.keyser.pt;

import java.util.stream.Stream;

public class Gain {

    private int gold;

    private int legend;

    public void apply(GainValue value, DeployedCard card) {
	this.gold = value.getGold(card);
	this.legend = value.getLegend(card);
    }

    public void setAll(Stream<Gain> gain) {
	this.gold = 0;
	this.legend = 0;
	gain.forEach(this::add);
    }

    private void add(Gain gain) {
	this.gold += gain.gold;
	this.legend += gain.legend;
    }

    public int getGold() {
	return gold;
    }

    public int getLegend() {
	return legend;
    }

    public void addLegend(int legend) {
	this.legend += legend;
    }
}
