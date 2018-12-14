package fr.keyser.pt2.effects;

import java.util.stream.Stream;

import fr.keyser.pt2.Slot;

public interface SelfEffect extends TargetableEffect {

    @Override
    public default Stream<Target> targets(Slot source) {
	return Stream.of(new Target(source.getCardPosition()));
    }

    public void apply(Slot source);

    @Override
    public default void apply(Slot source, ChoosenTargets targets) {
	apply(source);
    }
}
