package fr.keyser.pt.effects;

import java.util.stream.Stream;

import fr.keyser.pt.DeployedCard;

public interface SelfEffect extends TargetableEffect {

    @Override
    public default Stream<Target> targets(DeployedCard source) {
	return Stream.of(new Target(source.getPosition()));
    }

    public void apply(DeployedCard source);

    @Override
    public default void apply(DeployedCard source, ChoosenTargets targets) {
	apply(source);
    }
}
