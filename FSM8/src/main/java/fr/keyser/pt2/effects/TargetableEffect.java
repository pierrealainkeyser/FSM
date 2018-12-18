package fr.keyser.pt2.effects;

import java.util.stream.Stream;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt2.Slot;

public interface TargetableEffect {

    /**
     * Empty means no effect
     * 
     * @param source
     * @return
     */
    public default Stream<Target> targets(Slot source) {
	return Stream.empty();
    }

    public default Target target(CardPosition position) {
	return new Target(name(), position);
    }

    public default Target target(String name, CardPosition position) {
	return new Target(name() + "-" + name, position);
    }

    public default String name() {
	return getClass().getSimpleName();
    }

    public void apply(Slot source, ChoosenTargets targets);
}
