package fr.keyser.pt2.units;

import fr.keyser.pt2.PhaseEvent;
import fr.keyser.pt2.effects.DoAgeEffect;
import fr.keyser.pt2.effects.ShapeShift;
import fr.keyser.pt2.prop.ConstInt;

public class ShapeShifter extends Unit {
    public ShapeShifter() {
	super(ConstInt.ZERO);
	combat = ConstInt.FOUR;
	addEffect(PhaseEvent.INITIAL_DEPLOY, ShapeShift.INSTANCE);
	addEffect(PhaseEvent.INITIAL_DEPLOY, DoAgeEffect.INSTANCE);
    }
}
