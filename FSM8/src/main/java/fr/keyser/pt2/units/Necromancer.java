package fr.keyser.pt2.units;

import fr.keyser.pt2.PhaseEvent;
import fr.keyser.pt2.effects.AgeEveryoneButMeEffect;
import fr.keyser.pt2.prop.ConstInt;

public class Necromancer extends Unit {
    public Necromancer() {
	super(1);
	combat = ConstInt.FOUR;
	addEffect(PhaseEvent.INITIAL_DEPLOY, AgeEveryoneButMeEffect.INSTANCE);
    }
}
