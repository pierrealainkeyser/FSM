package fr.keyser.pt2.effects;

import java.util.List;
import java.util.stream.Stream;

import fr.keyser.pt2.Slot;

public interface SelfEffect extends TargetableEffect {

    @Override
    public default Stream<Target> targets(Slot source) {
	return Stream.of(target(source.getCardPosition()));
    }

    public List<EffectLog> apply(Slot source);

    @Override
    public default List<EffectLog> apply(Slot source, ChoosenTargets targets) {
	return apply(source);
    }
}
