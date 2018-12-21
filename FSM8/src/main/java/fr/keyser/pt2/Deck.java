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

    private final CardProvider provider;

    private final LinkedList<String> units = new LinkedList<>();

    private final List<String> discarded = new ArrayList<>();

    private final List<String> buildings = new ArrayList<>();

    public Deck(CardProvider provider) {
	this.provider = provider;
    }

    public List<Building> createBuildings() {
	return buildings.stream().map(provider::building).collect(Collectors.toList());
    }

    public void discard(String name) {
	discarded.add(name);
    }

    public DeckMemento getMemento() {
	DeckMemento d = new DeckMemento();
	d.setUnits(new ArrayList<>(units));
	d.setDiscardeds(new ArrayList<>(discarded));
	d.setBuildings(new ArrayList<>(buildings));
	return d;
    }

    public String next() {
	if (units.isEmpty()) {
	    units.addAll(discarded);
	    discarded.clear();
	    Collections.shuffle(units);
	}
	String name = units.removeFirst();
	return name;
    }

    public Unit nextUnit() {
	String name = next();
	return unit(name);
    }

    public void setMemento(DeckMemento memento) {
	units.clear();
	units.addAll(memento.getUnits());

	discarded.clear();
	discarded.addAll(memento.getDiscardeds());

	buildings.clear();
	buildings.addAll(memento.getBuildings());
    }

    private Unit track(String name, Unit unit) {
	DirtySupplier<CardPosition> position = unit.position();
	position.addListener(new DirtyListener() {
	    @Override
	    public void setDirty() {
		CardPosition cp = position.get();
		if (cp == null) {
		    discard(name);
		}
	    }
	});
	return unit;
    }

    public Unit unit(String name) {
	Unit unit = provider.unit(name);
	return track(name, unit);
    }
}
