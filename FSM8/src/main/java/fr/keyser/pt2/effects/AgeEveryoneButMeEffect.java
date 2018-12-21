package fr.keyser.pt2.effects;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.EffectLog;
import fr.keyser.pt2.Slot;

public class AgeEveryoneButMeEffect implements SelfEffect {

    public static final AgeEveryoneButMeEffect INSTANCE = new AgeEveryoneButMeEffect();

    private AgeEveryoneButMeEffect() {

    }

    @Override
    public List<EffectLog> apply(Slot slot) {
	Card me = slot.getCard().get();
	Stream<EffectLog> log = slot.getBoard().getUnits().filter(c -> c != me).map(c -> {
	    c.addAge(1);
	    return EffectLog.ageOther(me, 1, c);
	});
	return log.collect(Collectors.toList());
    }
}
