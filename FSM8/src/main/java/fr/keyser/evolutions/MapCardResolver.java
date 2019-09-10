package fr.keyser.evolutions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class MapCardResolver implements CardResolver {
    private Map<CardId, Card> cards = new LinkedHashMap<>();

    private int index = 0;

    public CardId card(Trait trait, int food) {
        CardId id = new CardId(index++);
        cards.put(id, new Card(trait, food));
        return id;
    }

    public Set<CardId> ids() {
        return cards.keySet();
    }

    @Override
    public Card resolve(CardId id) {
        return cards.get(id);
    }

}