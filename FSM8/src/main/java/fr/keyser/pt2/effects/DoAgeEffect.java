package fr.keyser.pt2.effects;

import java.util.Arrays;
import java.util.List;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.EffectLog;
import fr.keyser.pt2.Slot;

public class DoAgeEffect implements SelfEffect {

    public static final SelfEffect INSTANCE = new DoAgeEffect();

    private DoAgeEffect() {

    }

    @Override
    public List<EffectLog> apply(Slot slot) {
	Card card = slot.getCard().get();
	card.addAge(1);
	return Arrays.asList(EffectLog.age(card, 1));
    }
}
