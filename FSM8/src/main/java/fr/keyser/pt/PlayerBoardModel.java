package fr.keyser.pt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PlayerBoardModel {

    private int gold;

    private int legend;

    private Map<CardPosition, CardPositionSelector> inputActions = new HashMap<>();

    private List<MetaCard> toDraft = new ArrayList<>();

    private List<MetaCard> toDeploy = new ArrayList<>();

    private List<BuildingConstruction> buildPlan = new ArrayList<>();

    public int getGold() {
	return gold;
    }

    public void setGold(int gold) {
	this.gold = gold;
    }

    public int getLegend() {
	return legend;
    }

    public void setLegend(int legend) {
	this.legend = legend;
    }

    public void addLegend(int legend) {
	this.legend += legend;
	if (this.legend < 0)
	    this.legend = 0;
    }

    public void addGold(int gold) {
	this.gold += gold;
	if (this.gold < 0)
	    this.gold = 0;
    }

    public Map<CardPosition, CardPositionSelector> getInputActions() {
	return inputActions;
    }

    public List<MetaCard> getToDraft() {
	return toDraft;
    }

    public void setToDraft(List<MetaCard> toChoose) {
	this.toDraft = toChoose;
    }

    public List<MetaCard> getToDeploy() {
	return toDeploy;
    }

    public void setToDeploy(List<MetaCard> toDeploy) {
	this.toDeploy = toDeploy;
    }

    public List<BuildingConstruction> getBuildPlan() {
        return buildPlan;
    }

    public void setBuildPlan(List<BuildingConstruction> buildPlan) {
        this.buildPlan = buildPlan;
    }

}
