package fr.keyser.pt2.units;

import fr.keyser.pt2.PhaseEvent;
import fr.keyser.pt2.effects.DoAgeEffect;
import fr.keyser.pt2.prop.ConstInt;

public class CaveSpirit extends Unit {
    public CaveSpirit() {
	super(1);
	combat = ConstInt.FOUR;
	crystal = ConstInt.ONE;
	addEffect(PhaseEvent.INITIAL_DEPLOY, DoAgeEffect.INSTANCE);
    }
}
