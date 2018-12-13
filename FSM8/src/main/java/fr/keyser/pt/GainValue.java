package fr.keyser.pt;

public class GainValue {

    private final IntValue gold;

    private final IntValue legend;

    public GainValue(IntValue legend, IntValue gold) {
	this.legend = legend;
	this.gold = gold;
    }

    public int getLegend(DeployedCard ctx) {
	return legend.getValue(ctx);
    }

    public int getGold(DeployedCard ctx) {
	return gold.getValue(ctx);
    }

}
