package fr.keyser.pt;

import java.util.HashMap;
import java.util.Map;

public class CardDefinition {

    private final Map<String, Card> cards = new HashMap<>();

    public void add(Card card) {
	cards.put(card.getName(), card);
    }

    public Card card(CardModel model) {
	return card(model.getName());
    }

    public Card card(String name) {
	return cards.get(name);
    }

    public Unit unit(CardModel model) {
	return unit(model.getName());
    }

    public Unit unit(String name) {
	return (Unit) card(name);
    }

    public Building building(CardModel model) {
	return building(model.getName());
    }

    public Building building(String name) {
	return (Building) card(name);
    }
}
