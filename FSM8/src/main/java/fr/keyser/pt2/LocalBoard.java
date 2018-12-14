package fr.keyser.pt2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.CardPosition.Position;
import fr.keyser.pt2.prop.BoolSupplier;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.IntSupplier;
import fr.keyser.pt2.prop.PlugableInt;

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

    private final IntSupplier warGoldGain;

    private final IntSupplier payLegend;

    private final IntSupplier payGoldGain;

    private final IntSupplier ageLegend;

    private final IntSupplier ageGoldGain;

    private final Map<CardPosition, Slot> slots = new HashMap<>();

    private final PlugableInt victory = new PlugableInt();

    private final IntSupplier currentTurn;

    private final IntSupplier unitsAbove3;

    public LocalBoard(IntSupplier currentTurn) {
	this.currentTurn = currentTurn;
	addSlot(3, Position.FRONT);
	addSlot(2, Position.BACK);
	addSlot(4, Position.BUILDING);

	Collection<Slot> values = all();
	food = sum(values, Slot::getFood);
	wood = sum(values, Slot::getWood);
	crystal = sum(values, Slot::getCrystal);
	combat = sum(values, Slot::getEffectiveCombat);

	age = sum(values, Slot::getAge);
	dyingAgeToken = sum(values, Slot::getDyingAgeToken);

	deployLegend = sum(values, Slot::getDeployLegend);
	deployGoldGain = sum(values, Slot::getDeployGoldGain);

	warLegend = ConstInt.THREE.mult(victory).add(sum(values, Slot::getWarLegend));
	warGoldGain = sum(values, Slot::getWarGoldGain);

	payLegend = sum(values, Slot::getPayLegend);
	payGoldGain = ConstInt.TWO.add(sum(values, Slot::getPayGoldGain));

	ageLegend = sum(values, Slot::getAgeLegend);
	ageGoldGain = sum(values, Slot::getAgeGoldGain);

	unitsAbove3 = IntSupplier.count(units().map(Slot::getCombat), c -> c >= 3);
    }

    private void addSlot(int count, Position position) {
	for (int i = 0; i < count; ++i)
	    addSlot(new Slot(this, position.index(i)));
    }

    private void addSlot(Slot slot) {
	slots.put(slot.getCardPosition(), slot);
    }

    private Collection<Slot> all() {
	return slots.values();
    }

    public Slot back(int index) {
	return getSlot(Position.BACK.index(index));
    }

    public Slot building(int index) {
	return getSlot(Position.BUILDING.index(index));
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

    public Map<CardPosition, Slot> getSlots() {
	return slots;
    }

    public IntSupplier getUnitsAbove3() {
	return unitsAbove3;
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
	BoolSupplier dbl = combat.gte(opponent.combat.mult(ConstInt.TWO));

	victory.setSupplier(winWar(opponent).add(ConstInt.ONE.when(dbl)));
    }

    public void setNeighbours(LocalBoard left, LocalBoard right) {
	victory.setSupplier(winWar(left).add(winWar(right)));
    }

    @Override
    public String toString() {
	List<String> vals = new ArrayList<>();
	vals.add("Food : " + food);
	vals.add("Wood : " + wood);
	vals.add("Crystal : " + crystal);

	vals.add("Units above 3 strenght : " + unitsAbove3);
	vals.add("Combat : " + combat);
	vals.add("Victory : " + victory);

	vals.add("Deploy phase legend : " + deployLegend);
	vals.add("Deploy phase gold gain : " + deployGoldGain);

	vals.add("War phase legend : " + warLegend);
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
	return slots.values().stream().filter(s -> s.getCardPosition().isUnit());
    }

    private IntSupplier winWar(LocalBoard board) {
	return ConstInt.ONE.when(combat.gte(board.combat));
    }

}
