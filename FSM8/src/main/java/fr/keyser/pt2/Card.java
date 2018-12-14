package fr.keyser.pt2;

import java.util.function.Function;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt2.prop.BoolSupplier;
import fr.keyser.pt2.prop.ConstBool;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.DirtySupplier;
import fr.keyser.pt2.prop.IntSupplier;
import fr.keyser.pt2.prop.MutableBool;
import fr.keyser.pt2.prop.MutableInt;
import fr.keyser.pt2.prop.MutableProp;
import fr.keyser.pt2.units.Unit;

public abstract class Card {

    private final MutableProp<LocalBoard> board = new MutableProp<>();

    protected IntSupplier combat = ConstInt.ZERO;
    protected IntSupplier warLegend = ConstInt.ZERO;
    protected IntSupplier warGoldGain = ConstInt.ZERO;

    private final MutableInt deployLegend = new MutableInt();
    private final MutableInt deployGoldGain = new MutableInt();

    protected IntSupplier payLegend = ConstInt.ZERO;
    protected IntSupplier payGoldGain = ConstInt.ZERO;

    protected IntSupplier ageLegend = ConstInt.ZERO;
    protected IntSupplier ageGoldGain = ConstInt.ZERO;

    protected IntSupplier food = ConstInt.ZERO;
    protected IntSupplier wood = ConstInt.ZERO;
    protected IntSupplier crystal = ConstInt.ZERO;

    private final MutableInt buildLevel = new MutableInt();

    private final MutableInt deployedTurn = new MutableInt();
    private final IntSupplier currentTurn = mapInt(LocalBoard::getCurrentTurn);
    private final BoolSupplier justDeployed = deployedTurn.eq(currentTurn);

    private final MutableInt age = new MutableInt();
    protected final MutableInt tokenToDie = new MutableInt(1);
    private final MutableBool simpleDyingProtection = new MutableBool(false);
    private final BoolSupplier isUnit = new ConstBool(this instanceof Unit);
    private final BoolSupplier willLive = isUnit.and(age.lt(tokenToDie).or(age.eq(ConstInt.ONE).and(simpleDyingProtection)));
    private final BoolSupplier willDie = isUnit.and(willLive.not());
    private final IntSupplier dyingAgeToken = age.when(willDie);

    protected final MutableProp<CardPosition> position = new MutableProp<>();
    protected BoolSupplier mayCombat = position.match(CardPosition::mayCombat);

    public final void deploy() {
	deployedTurn.set(currentTurn.get());
    }

    public final MutableInt getAge() {
	return age;
    }

    public final IntSupplier getAgeGoldGain() {
	return ageGoldGain;
    }

    public final IntSupplier getAgeLegend() {
	return ageLegend;
    }

    public final MutableInt getBuildLevel() {
	return buildLevel;
    }

    public final IntSupplier getCombat() {
	return combat;
    }

    public final IntSupplier getCrystal() {
	return crystal;
    }

    public final MutableInt getDeployGoldGain() {
	return deployGoldGain;
    }

    public final MutableInt getDeployLegend() {
	return deployLegend;
    }

    public final IntSupplier getDyingAgeToken() {
	return dyingAgeToken;
    }

    public final IntSupplier getFood() {
	return food;
    }

    public final BoolSupplier getJustDeployed() {
	return justDeployed;
    }

    public final BoolSupplier getMayCombat() {
	return mayCombat;
    }

    public CardMemento getMemento() {
	CardMemento cm = new CardMemento();
	cm.setAge(age.getValue());
	cm.setBuildLevel(buildLevel.getValue());
	cm.setPosition(position.get());
	cm.setDeployedTurn(deployedTurn.getValue());
	cm.setSimpleDyingProtection(simpleDyingProtection.getValue());
	return cm;
    }

    public final IntSupplier getPayGoldGain() {
	return payGoldGain;
    }

    public final IntSupplier getPayLegend() {
	return payLegend;
    }

    public final MutableBool getSimpleDyingProtection() {
	return simpleDyingProtection;
    }

    public final IntSupplier getWarGoldGain() {
	return warGoldGain;
    }

    public final IntSupplier getWarLegend() {
	return warLegend;
    }

    public final BoolSupplier getWillDie() {
	return willDie;
    }

    public BoolSupplier getWillLive() {
	return willLive;
    }

    public final IntSupplier getWood() {
	return wood;
    }

    protected BoolSupplier mapBool(Function<LocalBoard, BoolSupplier> accessor) {
	return board.mapBool(accessor);
    }

    protected IntSupplier mapInt(Function<LocalBoard, IntSupplier> accessor) {
	return board.mapInt(accessor);
    }

    public final DirtySupplier<CardPosition> position() {
	return position;
    }

    public void setAgeGoldGain(IntSupplier ageGoldGain) {
	this.ageGoldGain = ageGoldGain;
    }

    public void setBoard(LocalBoard board) {
	this.board.set(board);
    }

    public void setMemento(CardMemento m) {
	age.setValue(m.getAge());
	buildLevel.setValue(m.getBuildLevel());
	position.set(m.getPosition());
	deployedTurn.set(m.getDeployedTurn());
	simpleDyingProtection.set(m.isSimpleDyingProtection());
    }

    public final void setPosition(CardPosition position) {
	this.position.set(position);
    }

    public final void undeploy() {
	deployedTurn.set(null);
    }
}
