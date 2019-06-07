package fr.keyser.pt2.units;

import fr.keyser.pt2.PhaseEvent;
import fr.keyser.pt2.effects.PreserveFromDeathEffect;
import fr.keyser.pt2.prop.ConstInt;

public class Mystic extends Unit {
    public Mystic() {
	super(ConstInt.ZERO);
	addEffect(PhaseEvent.AGING, PreserveFromDeathEffect.INSTANCE);
    }
}
