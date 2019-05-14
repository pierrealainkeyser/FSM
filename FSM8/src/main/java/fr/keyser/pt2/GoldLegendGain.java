package fr.keyser.pt2;

public class GoldLegendGain {

    private final int gold;

    private final int legend;

    public GoldLegendGain(int gold, int legend) {
	this.gold = gold;
	this.legend = legend;
    }

    public int getGold() {
	return gold;
    }

    public int getLegend() {
	return legend;
    }

    @Override
    public String toString() {
	return "+{gold=" + gold + ", legend=" + legend + "}";
    }

}
