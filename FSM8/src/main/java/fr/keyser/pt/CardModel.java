package fr.keyser.pt;

import java.util.HashMap;
import java.util.Map;

public final class CardModel {

    private MetaCard meta;

    private int ageToken;

    private int playedTurn;

    private BuildingLevel level;

    private Map<String, CardPosition> selected;

    private boolean shapeShifted;

    public MetaCard getMeta() {
	return meta;
    }

    public void setMeta(MetaCard meta) {
	this.meta = meta;
    }

    public int getAgeToken() {
	return ageToken;
    }

    public void setAgeToken(int ageToken) {
	this.ageToken = ageToken;
    }

    public int getPlayedTurn() {
	return playedTurn;
    }

    public void setPlayedTurn(int playedTurn) {
	this.playedTurn = playedTurn;
    }

    public BuildingLevel getLevel() {
	return level;
    }

    public void setLevel(BuildingLevel level) {
	this.level = level;
    }

    public Map<String, CardPosition> getSelected() {
	return selected;
    }

    public void addPositionFor(CardPosition position, String name) {
	if (selected == null)
	    selected = new HashMap<>();

	selected.put(name, position);
    }

    public void setSelected(Map<String, CardPosition> selected) {
	this.selected = selected;
    }

    public boolean isShapeShifted() {
        return shapeShifted;
    }

    public void setShapeShifted(boolean shapeShifted) {
        this.shapeShifted = shapeShifted;
    }
}
