package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.PhaseEvent;
import fr.keyser.pt2.effects.GainLegendEffect;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.IntSupplier;

public class Noble extends Unit {
    public Noble() {
	super(ConstInt.THREE);
	crystal = ConstInt.ONE;

	IntSupplier above2 = mapInt(LocalBoard::getUnitsCostAbove2);
	addEffect(PhaseEvent.INITIAL_DEPLOY, new GainLegendEffect(above2.mult(ConstInt.TWO)));
    }
}
