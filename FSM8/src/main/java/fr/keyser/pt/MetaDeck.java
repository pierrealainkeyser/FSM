package fr.keyser.pt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetaDeck {

    private List<MetaCard> cards = new ArrayList<>();

    private List<MetaCard> discarded = new ArrayList<>();

    private List<Building> buildings = new ArrayList<>();

    public void load(Card card, int count) {
	for (int i = 0; i < count; ++i) {
	    int size = cards.size();
	    cards.add(new MetaCard(size, card));
	}
    }

    public List<MetaCard> getCards() {
	return cards;
    }

    public void setCards(List<MetaCard> cards) {
	this.cards = cards;
    }

    public List<MetaCard> getDiscarded() {
	return discarded;
    }

    public void setDiscarded(List<MetaCard> discarded) {
	this.discarded = discarded;
    }

    public List<Building> getBuildings() {
	return Collections.unmodifiableList(buildings);
    }

    public void setBuildings(List<Building> buildings) {
	this.buildings = buildings;
    }
}
