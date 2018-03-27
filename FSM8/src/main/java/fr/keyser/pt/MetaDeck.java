package fr.keyser.pt;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MetaDeck {

    public static MetaDeck createDefault() {
	MetaDeck deck = new MetaDeck();
	DefaultDeckBuilder.DEFAULT.loadBuilding(deck);
	DefaultDeckBuilder.DEFAULT.loadUnits(deck);
	Collections.shuffle(deck.cards);
	return deck;
    }

    private List<MetaCard> cards = new LinkedList<>();

    private List<MetaCard> discarded = new LinkedList<>();

    private List<MetaCard> buildings = new LinkedList<>();

    public void load(Unit card, int count) {
	for (int i = 0; i < count; ++i) {
	    int size = cards.size();
	    cards.add(new MetaCard(size, card));
	}
    }

    public void load(Building building) {
	int size = buildings.size();
	buildings.add(new MetaCard(size, building));
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

    public List<MetaCard> getBuildings() {
	return Collections.unmodifiableList(buildings);
    }

    public void setBuildings(List<MetaCard> buildings) {
	this.buildings = buildings;
    }
}
