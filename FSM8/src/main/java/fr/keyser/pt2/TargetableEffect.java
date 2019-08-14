package fr.keyser.pt2;

import java.util.List;
import java.util.stream.Stream;

import fr.keyser.pt.CardPosition;

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

    public default String name() {
	return getClass().getSimpleName();
    }

    public List<EffectLog> apply(Slot source, ChoosenTargets targets);
}
