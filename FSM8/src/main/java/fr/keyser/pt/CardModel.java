package fr.keyser.pt;

import java.util.HashMap;
import java.util.Map;

public final class CardModel {

    private int ageToken;

    private BuildingLevel level;

    private MetaCard meta;

    private int playedTurn;

    private Map<String, CardPosition> selected;

    private boolean shapeShifted;

    public CardModel() {
    }

    public CardModel(MetaCard meta, BuildingLevel level) {
	this.meta = meta;
	this.level = level;
    }

    public CardModel(MetaCard meta, int ageToken, int playedTurn) {
	this.meta = meta;
	this.ageToken = ageToken;
	this.playedTurn = playedTurn;
    }

    public void addPositionFor(CardPosition position, String name) {
	if (selected == null)
	    selected = new HashMap<>();

	selected.put(name, position);
    }

    public int getAgeToken() {
	return ageToken;
    }

    public BuildingLevel getLevel() {
	return level;
    }

    public MetaCard getMeta() {
	return meta;
    }

    public int getPlayedTurn() {
	return playedTurn;
    }

    public Map<String, CardPosition> getSelected() {
	return selected;
    }

    public boolean isShapeShifted() {
        return shapeShifted;
    }

    public void setAgeToken(int ageToken) {
	this.ageToken = ageToken;
    }

    public void setLevel(BuildingLevel level) {
	this.level = level;
    }

    public void setMeta(MetaCard meta) {
	this.meta = meta;
    }

    public void setPlayedTurn(int playedTurn) {
	this.playedTurn = playedTurn;
    }

    public void setSelected(Map<String, CardPosition> selected) {
	this.selected = selected;
    }

    public void setShapeShifted(boolean shapeShifted) {
        this.shapeShifted = shapeShifted;
    }
}
