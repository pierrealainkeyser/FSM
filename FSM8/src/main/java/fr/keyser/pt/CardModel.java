package fr.keyser.pt;

import java.util.HashMap;
import java.util.Map;

public final class CardModel {

    private String name;

    private int ageToken;

    private int playedTurn;

    private BuildingLevel level;

    private Map<String, CardPosition> selected;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
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

    public CardPosition positionFor(String name) {
	if (selected == null) {
	    CardPosition found = selected.remove(name);
	    if (selected.isEmpty())
		selected = null;
	    return found;
	} else
	    return null;
    }

    public void setSelected(Map<String, CardPosition> selected) {
	this.selected = selected;
    }
}
