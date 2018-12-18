package fr.keyser.pt2.effects;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.Slot;

public class AgeEveryoneButMeEffect implements SelfEffect {

    public static final AgeEveryoneButMeEffect INSTANCE = new AgeEveryoneButMeEffect();

    private AgeEveryoneButMeEffect() {

    }

    @Override
    public void apply(Slot slot) {
	Card me = slot.getCard().get();
	slot.getBoard().getUnits().filter(c -> c != me).forEach(c -> c.addAge(1));
    }
}
