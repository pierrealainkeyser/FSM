package fr.keyser.pt2;

import java.util.List;

public class PlayerView {
    private int gold;

    private int legend;

    private List<CardMemento> cards;

    private int food;

    private int wood;

    private int crystal;
    private int combat;
    private int victory;

    private GoldLegendGain deploy;

    private GoldLegendGain war;

    private GoldLegendGain pay;

    private GoldLegendGain age;

    public PlayerView(ResourcesStats stats, PlayerScoreMemento player) {
	gold = player.getGold();
	legend = player.getLegend();
	cards = player.getCards();
	food = stats.getFood();
	wood = stats.getWood();
	crystal = stats.getCrystal();
	combat = stats.getCombat();
	victory = stats.getVictory();
	age = new GoldLegendGain(stats.getAgeGoldGain(), stats.getAgeLegend());
	deploy = new GoldLegendGain(stats.getDeployGoldGain(), stats.getDeployLegend());
	pay = new GoldLegendGain(stats.getPayGoldGain(), stats.getPayLegend());
	war = new GoldLegendGain(stats.getWarGoldGain(), stats.getWarLegend());
    }

    @Override
    public String toString() {
	return "PlayerView [wood=" + getWood() + ", food=" + getFood() + ", crystal=" + getCrystal() + ", combat="
	        + getCombat() + ", victory=" + getVictory() + ", deployGain=" + getDeploy() + ", warGain=" + getWar()
	        + ", payGain=" + getPay() + ", ageGain=" + getAge() + ", legend=" + getLegend() + ", gold="
	        + getGold() + ", cards=" + getCards() + "]";
    }

    public int getGold() {
	return gold;
    }

    public int getLegend() {
	return legend;
    }

    public List<CardMemento> getCards() {
	return cards;
    }

    public int getFood() {
	return food;
    }

    public int getWood() {
	return wood;
    }

    public int getCrystal() {
	return crystal;
    }

    public int getCombat() {
	return combat;
    }

    public GoldLegendGain getDeploy() {
	return deploy;
    }

    public GoldLegendGain getWar() {
	return war;
    }

    public GoldLegendGain getPay() {
	return pay;
    }

    public GoldLegendGain getAge() {
	return age;
    }

    public int getVictory() {
	return victory;
    }
}
