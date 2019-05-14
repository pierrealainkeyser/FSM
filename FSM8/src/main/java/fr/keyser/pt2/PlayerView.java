package fr.keyser.pt2;

import java.util.List;

public class PlayerView {
    private final PlayerMemento player;

    private final ResourcesStats stats;

    public PlayerView(ResourcesStats stats, PlayerMemento player) {
	this.stats = stats;
	this.player = player;
    }

    public GoldLegendGain getAgeGain() {
	return new GoldLegendGain(stats.getAgeGoldGain(), stats.getAgeLegend());
    }

    public List<CardMemento> getCards() {
	return player.getCards();
    }

    public int getCombat() {
	return stats.getCombat();
    }

    public int getCrystal() {
	return stats.getCrystal();
    }

    public GoldLegendGain getDeployGain() {
	return new GoldLegendGain(stats.getDeployGoldGain(), stats.getDeployLegend());
    }

    public int getFood() {
	return stats.getFood();
    }

    public int getGold() {
	return player.getGold();
    }

    public int getLegend() {
	return player.getLegend();
    }

    public GoldLegendGain getPayGain() {
	return new GoldLegendGain(stats.getPayGoldGain(), stats.getPayLegend());
    }

    public int getVictory() {
	return stats.getVictory();
    }

    public GoldLegendGain getWarGain() {
	return new GoldLegendGain(stats.getWarGoldGain(), stats.getWarLegend());
    }

    public int getWood() {
	return stats.getWood();
    }

    void setGold(int gold) {
	player.setGold(gold);
    }

    void setLegend(int legend) {
	player.setLegend(legend);
    }

    @Override
    public String toString() {
	return "PlayerStats [wood=" + getWood() + ", food=" + getFood() + ", crystal=" + getCrystal() + ", combat="
	        + getCombat() + ", victory=" + getVictory() + ", deployGain=" + getDeployGain() + ", warGain=" + getWarGain()
	        + ", payGain=" + getPayGain() + ", ageGain=" + getAgeGain() + ", legend=" + getLegend() + ", gold="
	        + getGold() + "]";
    }
}
