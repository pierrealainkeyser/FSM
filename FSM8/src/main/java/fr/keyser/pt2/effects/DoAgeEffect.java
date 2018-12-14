package fr.keyser.pt2.effects;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.Slot;

public class DoAgeEffect implements SelfEffect {

    public static final SelfEffect INSTANCE = new DoAgeEffect();

    private DoAgeEffect() {

    }

    @Override
    public void apply(Slot slot) {
	Card card = slot.getCard().get();
	card.addAge(1);
    }
}
