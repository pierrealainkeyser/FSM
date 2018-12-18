package fr.keyser.pt2.units;

import fr.keyser.pt2.PhaseEvent;
import fr.keyser.pt2.effects.DoAgeEffect;
import fr.keyser.pt2.prop.ConstInt;

public class Devil extends Unit {
    public Devil() {
	super(-2);
	combat = ConstInt.TWO;
	ageLegend = ConstInt.MINUS_TWO.when(getWillDie().and(getAge().eq(ConstInt.ONE)));
	addEffect(PhaseEvent.INITIAL_DEPLOY, DoAgeEffect.INSTANCE);
    }
}
