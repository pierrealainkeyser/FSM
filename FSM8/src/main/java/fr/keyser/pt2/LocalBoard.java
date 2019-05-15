package fr.keyser.pt2;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.CardPosition.Position;
import fr.keyser.pt2.buildings.Building;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.IntSupplier;
import fr.keyser.pt2.prop.PlugableInt;
import fr.keyser.pt2.units.Unit;

public final class LocalBoard {

    private static <T> IntSupplier sum(Collection<T> slot, Function<T, IntSupplier> supplier) {
	return IntSupplier.accumulate(slot.stream().map(supplier), (l, r) -> l + r);
    }

    private final IntSupplier food;

    private final IntSupplier wood;

    private final IntSupplier crystal;

    private final IntSupplier age;

    private final IntSupplier dyingAgeToken;

    private final IntSupplier combat;

    private final IntSupplier deployLegend;

    private final IntSupplier deployGoldGain;

    private final IntSupplier warLegend;

    private final IntSupplier buildingWarLegend;

    private final IntSupplier warGoldGain;

    private final IntSupplier payLegend;

    private final IntSupplier payGoldGain;

    private final IntSupplier ageLegend;

    private final IntSupplier ageGoldGain;

    private final Map<CardPosition, Slot> slots = new LinkedHashMap<>();

    private final PlugableInt victory = new PlugableInt();

    private final IntSupplier currentTurn;

    private final IntSupplier unitsStrenghtAbove3;

    private final IntSupplier unitsCostAbove2;

    private final IntSupplier differentRessourcesCount;

    public LocalBoard(IntSupplier currentTurn) {
	this.currentTurn = currentTurn;
	addSlot(3, Position.FRONT);
	addSlot(2, Position.BACK);
	addSlot(4, Position.BUILDING);

	Collection<Slot> values = all();
	List<Slot> buildings = buildings().collect(toList());
	List<Slot> units = units().collect(toList());

	food = sum(values, Slot::getFood);
	wood = sum(values, Slot::getWood);
	crystal = sum(values, Slot::getCrystal);
	combat = sum(values, Slot::getEffectiveCombat);

	age = sum(units, Slot::getAge);
	dyingAgeToken = sum(units, Slot::getDyingAgeToken);

	deployLegend = sum(values, Slot::getDeployLegend);
	deployGoldGain = sum(values, Slot::getDeployGoldGain);

	buildingWarLegend = sum(buildings, Slot::getWarLegend);
	warLegend = ConstInt.THREE.mult(victory).add(sum(values, Slot::getWarLegend));
	warGoldGain = sum(values, Slot::getWarGoldGain);

	payLegend = sum(values, Slot::getPayLegend);
	payGoldGain = ConstInt.TWO.add(sum(values, Slot::getPayGoldGain));

	ageLegend = sum(values, Slot::getAgeLegend);
	ageGoldGain = sum(values, Slot::getAgeGoldGain);

	unitsStrenghtAbove3 = IntSupplier.count(units.stream().map(Slot::getCombat), c -> c >= 3);
	unitsCostAbove2 = IntSupplier.count(units.stream().map(Slot::getCost), c -> c >= 2);

	differentRessourcesCount = ConstInt.ONE.when(food.gte(ConstInt.ONE))
	        .add(ConstInt.ONE.when(wood.gte(ConstInt.ONE)))
	        .add(ConstInt.ONE.when(crystal.gte(ConstInt.ONE)));
    }

    private void addSlot(int count, Position position) {
	for (int i = 0; i < count; ++i)
	    addSlot(new Slot(this, position.index(i)));
    }

    private void addSlot(Slot slot) {
	slots.put(slot.getCardPosition(), slot);
    }

    public Collection<Slot> all() {
	return slots.values();
    }

    public Slot back(int index) {
	return getSlot(Position.BACK.index(index));
    }

    public Slot building(int index) {
	return getSlot(Position.BUILDING.index(index));
    }

    private Stream<Slot> buildings() {
	return slots.values().stream().filter(s -> !s.getCardPosition().isUnit());
    }

    public Slot front(int index) {
	return getSlot(Position.FRONT.index(index));
    }

    public IntSupplier getAge() {
	return age;
    }

    public IntSupplier getAgeGoldGain() {
	return ageGoldGain;
    }

    public IntSupplier getAgeLegend() {
	return ageLegend;
    }

    public Stream<Building> getBuildings() {
	return slots.values().stream().map(s -> s.getCard().get()).filter(c -> c instanceof Building).map(c -> (Building) c);
    }

    public IntSupplier getBuildingWarLegend() {
	return buildingWarLegend;
    }

    public IntSupplier getCombat() {
	return combat;
    }

    public IntSupplier getCrystal() {
	return crystal;
    }

    public IntSupplier getCurrentTurn() {
	return currentTurn;
    }

    public IntSupplier getDeployGoldGain() {
	return deployGoldGain;
    }

    public IntSupplier getDeployLegend() {
	return deployLegend;
    }

    public IntSupplier getDifferentRessourcesCount() {
	return differentRessourcesCount;
    }

    public IntSupplier getDyingAgeToken() {
	return dyingAgeToken;
    }

    public IntSupplier getFood() {
	return food;
    }

    public IntSupplier getPayGoldGain() {
	return payGoldGain;
    }

    public IntSupplier getPayLegend() {
	return payLegend;
    }

    public Slot getSlot(CardPosition position) {
	return slots.get(position);
    }

    public Stream<Unit> getUnits() {
	return slots.values().stream().map(s -> s.getCard().get()).filter(c -> c instanceof Unit).map(c -> (Unit) c);
    }

    public IntSupplier getUnitsCostAbove2() {
	return unitsCostAbove2;
    }

    public IntSupplier getUnitsStrenghtAbove3() {
	return unitsStrenghtAbove3;
    }

    public IntSupplier getVictory() {
	return victory;
    }

    public IntSupplier getWarGoldGain() {
	return warGoldGain;
    }

    public IntSupplier getWarLegend() {
	return warLegend;
    }

    public IntSupplier getWood() {
	return wood;
    }

    public void setNeighbour(LocalBoard opponent) {
	IntSupplier twice = opponent.combat.mult(ConstInt.TWO);
	setNeighbours(opponent.combat, twice);
    }

    public void setNeighbours(IntSupplier left, IntSupplier right) {
	victory.setSupplier(winWar(left).add(winWar(right)));
    }

    public void setNeighbours(LocalBoard left, LocalBoard right) {
	setNeighbours(left.combat, right.combat);
    }

    public ResourcesStats stats() {
	ResourcesStats r = new ResourcesStats();

	r.setAge(age.getValue());
	r.setAgeGoldGain(ageGoldGain.getValue());
	r.setAgeLegend(ageLegend.getValue());
	r.setCombat(combat.getValue());
	r.setCrystal(crystal.getValue());
	r.setDeployGoldGain(deployGoldGain.getValue());
	r.setDeployLegend(deployLegend.getValue());
	r.setDyingAgeToken(dyingAgeToken.getValue());
	r.setFood(food.getValue());
	r.setPayGoldGain(payGoldGain.getValue());
	r.setPayLegend(payLegend.getValue());
	r.setWarGoldGain(warGoldGain.getValue());
	r.setWarLegend(warLegend.getValue());
	r.setWood(wood.getValue());
	r.setVictory(victory.getValue());
	return r;
    }

    public List<CardMemento> memento() {
	return all().stream().flatMap(Slot::memento).collect(Collectors.toList());
    }

    @Override
    public String toString() {
	List<String> vals = new ArrayList<>();
	vals.add("Food : " + food);
	vals.add("Wood : " + wood);
	vals.add("Crystal : " + crystal);
	vals.add("Different resources : " + differentRessourcesCount);

	vals.add("Units above 3 strength : " + unitsStrenghtAbove3);
	vals.add("Combat : " + combat);
	vals.add("Victory : " + victory);

	vals.add("Deploy phase legend : " + deployLegend);
	vals.add("Deploy phase gold gain : " + deployGoldGain);

	vals.add("War phase legend : " + warLegend + " (" + buildingWarLegend + ")");
	vals.add("War phase gold gain : " + warGoldGain);

	vals.add("Pay phase legend : " + payLegend);
	vals.add("Pay phase gold gain : " + payGoldGain);

	vals.add("Age phase legend : " + ageLegend);
	vals.add("Age phase gold gain : " + ageGoldGain);

	vals.add("Age token : " + age);
	vals.add("Dying age token : " + dyingAgeToken);

	return vals.stream().collect(Collectors.joining("\n"));
    }

    private Stream<Slot> units() {
	return all().stream().filter(s -> s.getCardPosition().isUnit());
    }

    private IntSupplier winWar(IntSupplier combat) {
	return ConstInt.ONE.when(this.combat.gte(combat));
    }
}
