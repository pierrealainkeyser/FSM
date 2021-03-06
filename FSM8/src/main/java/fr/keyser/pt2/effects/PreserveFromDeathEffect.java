package fr.keyser.pt2.effects;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.stream.Stream;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.EffectLog;
import fr.keyser.pt2.Slot;
import fr.keyser.pt2.Target;

public class PreserveFromDeathEffect implements MonoEffect {

    public static final PreserveFromDeathEffect INSTANCE = new PreserveFromDeathEffect();

    private PreserveFromDeathEffect() {

    }

    @Override
    public Stream<Target> targets(Slot source) {
	return source.getBoard().getUnits().filter(c -> c.getWillDie().getValue()).map(dc -> target(dc.getPosition()));
    }

    @Override
    public List<EffectLog> apply(Slot source, Card target) {
	target.getSimpleDyingProtection().setValue(true);
	return asList(EffectLog.preserve(source.getCard().get(), target));
    }

}
