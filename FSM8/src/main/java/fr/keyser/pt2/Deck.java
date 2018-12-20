package fr.keyser.pt2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt2.buildings.Building;
import fr.keyser.pt2.prop.DirtyListener;
import fr.keyser.pt2.prop.DirtySupplier;
import fr.keyser.pt2.units.Unit;

public class Deck {

    private CardProvider provider;

    private final LinkedList<String> units = new LinkedList<>();

    private final List<String> discarded = new ArrayList<>();

    private final List<String> buildings = new ArrayList<>();

    public DeckMemento getMemento() {
	DeckMemento d = new DeckMemento();
	d.setUnits(new ArrayList<>(units));
	d.setDiscardeds(new ArrayList<>(discarded));
	d.setBuildings(new ArrayList<>(buildings));
	return d;
    }

    public void setMemento(DeckMemento memento) {
	units.clear();
	units.addAll(memento.getUnits());

	discarded.clear();
	discarded.addAll(memento.getDiscardeds());

	buildings.clear();
	buildings.addAll(memento.getBuildings());
    }

    public Card restore(CardMemento memento) {
	Card card = null;
	String name = memento.getName();
	if (memento.getBuildLevel() > 0)
	    card = provider.building(name);
	else
	    card = track(name, provider.unit(name));
	card.setMemento(memento);
	return card;
    }

    public Unit next() {
	if (units.isEmpty()) {
	    units.addAll(discarded);
	    discarded.clear();
	    Collections.shuffle(units);
	}
	String name = units.removeFirst();
	Unit unit = provider.unit(name);
	return track(name, unit);
    }

    private Unit track(String name, Unit unit) {
	DirtySupplier<CardPosition> position = unit.position();
	position.addListener(new DirtyListener() {

	    @Override
	    public void setDirty() {
		CardPosition cp = position.get();
		if (cp == null) {
		    discarded.add(name);
		}
	    }
	});
	return unit;
    }

    public List<Building> createBuildings() {
	return buildings.stream().map(provider::building).collect(Collectors.toList());
    }
}
