package fr.keyser.pt2;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import fr.keyser.pt2.buildings.Building;
import fr.keyser.pt2.units.Unit;

public class CardProvider {

    private int nextId = 1;

    private final Map<String, Supplier<Unit>> units = new HashMap<>();

    private final Map<String, Supplier<Building>> buildings = new HashMap<>();

    public CardProvider addUnit(Supplier<Unit> supplier) {
	units.put(supplier.get().getName(), supplier);
	return this;
    }

    public CardProvider addBuilding(Supplier<Building> supplier) {
	buildings.put(supplier.get().getName(), supplier);
	return this;
    }

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
