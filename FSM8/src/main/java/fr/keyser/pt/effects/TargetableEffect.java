package fr.keyser.pt.effects;

import java.util.stream.Stream;

import fr.keyser.pt.DeployedCard;

public interface TargetableEffect {

    /**
     * Empty means no effect
     * 
     * @param source
     * @return
     */
    public default Stream<Target> targets(DeployedCard source) {
	return Stream.empty();
    }

    public void apply(DeployedCard source, ChoosenTargets targets);
}
