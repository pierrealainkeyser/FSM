package fr.keyser.pt2.view;

import java.util.List;

import fr.keyser.pt2.effects.EffectLog;

public class PartialLocalPlayerView {

    private boolean done;

    private int gold;

    private int legend;

    private List<EffectLog> logs;

    private String state;

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

    public List<EffectLog> getLogs() {
	return logs;
    }

    public void setLogs(List<EffectLog> logs) {
	this.logs = logs;
    }

    public boolean isDone() {
	return done;
    }

    public void setDone(boolean done) {
	this.done = done;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
