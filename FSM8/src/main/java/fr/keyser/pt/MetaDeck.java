package fr.keyser.pt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.keyser.pt.buildings.Casern;
import fr.keyser.pt.buildings.Mine;
import fr.keyser.pt.buildings.Tavern;
import fr.keyser.pt.buildings.Temple;
import fr.keyser.pt.buildings.Town;
import fr.keyser.pt.units.CaveSpirit;
import fr.keyser.pt.units.ForestChildren;
import fr.keyser.pt.units.Lumberjack;
import fr.keyser.pt.units.Miner;
import fr.keyser.pt.units.Necromancer;
import fr.keyser.pt.units.Sculptrice;
import fr.keyser.pt.units.Treeman;
import fr.keyser.pt.units.Veteran;

public class MetaDeck {

    public static MetaDeck createDefault() {
	MetaDeck deck = new MetaDeck();

	deck.load(new Town());
	deck.load(new Casern());
	deck.load(new Temple());
	deck.load(new Mine());
	deck.load(new Tavern());

	deck.load(new Lumberjack(), 3);
	deck.load(new ForestChildren(), 3);
	deck.load(new Miner(), 3);
	deck.load(new Treeman(), 2);
	deck.load(new Veteran(), 3);
	deck.load(new Sculptrice(), 2);
	deck.load(new CaveSpirit(), 2);
	deck.load(new Necromancer(), 1);
	Collections.shuffle(deck.cards);
	return deck;
    }

    private List<MetaCard> cards = new ArrayList<>();

    private List<MetaCard> discarded = new ArrayList<>();

    private List<MetaCard> buildings = new ArrayList<>();

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
