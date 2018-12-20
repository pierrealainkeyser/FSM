package fr.keyser.pt2;

import java.util.List;

public class DeckMemento {

    private List<String> units;

    private List<String> discardeds;

    private List<String> buildings;

    public List<String> getUnits() {
	return units;
    }

    public void setUnits(List<String> units) {
	this.units = units;
    }

    public List<String> getDiscardeds() {
	return discardeds;
    }

    public void setDiscardeds(List<String> discarded) {
	this.discardeds = discarded;
    }

    public List<String> getBuildings() {
	return buildings;
    }

    public void setBuildings(List<String> buildings) {
	this.buildings = buildings;
    }
}
