package fr.keyser.pt2.units;

import fr.keyser.pt2.PhaseEvent;
import fr.keyser.pt2.effects.UpgradeBuildingsToLevel2Effect;
import fr.keyser.pt2.prop.ConstInt;

public class Golem extends Unit {
    public Golem() {
	super(ConstInt.TWO);
	combat = ConstInt.THREE;
	addEffect(PhaseEvent.INITIAL_DEPLOY, UpgradeBuildingsToLevel2Effect.INSTANCE);
    }
}
