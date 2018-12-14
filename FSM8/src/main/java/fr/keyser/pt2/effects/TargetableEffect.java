package fr.keyser.pt2.effects;

import java.util.stream.Stream;

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

    public void apply(Slot source, ChoosenTargets targets);
}
