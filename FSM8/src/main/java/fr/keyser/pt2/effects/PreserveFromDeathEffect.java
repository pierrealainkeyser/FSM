package fr.keyser.pt2.effects;

import java.util.stream.Stream;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.Slot;

public class PreserveFromDeathEffect implements MonoEffect {

    @Override
    public Stream<Target> targets(Slot source) {
	return source.getBoard().getUnits().filter(c -> c.getWillDie().getValue()).map(dc -> target(dc.getPosition()));
    }

    @Override
    public void apply(Slot source, Card target) {
	target.getSimpleDyingProtection().setValue(true);
    }

}
