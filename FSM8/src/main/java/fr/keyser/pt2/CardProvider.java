package fr.keyser.pt2;

import java.util.Map;
import java.util.function.Supplier;

import fr.keyser.pt2.buildings.Building;
import fr.keyser.pt2.units.Unit;

public class CardProvider {

    private int nextId = 1;

    private Map<String, Supplier<Unit>> units;

    private Map<String, Supplier<Building>> buildings;

    public Unit unit(String name) {
	Supplier<Unit> s = units.get(name);
	Unit unit = s.get();
	unit.setId(nextId++);
	return unit;
    }
    
    public Building building(String name) {
	Supplier<Building> s = buildings.get(name);
	Building building = s.get();
	building.setId(nextId++);
	return building;
    }

}
