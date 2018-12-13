package fr.keyser.pt.effects;

import java.util.stream.Stream;

import fr.keyser.pt.DeployedCard;

public class PreserveFromDeathEffect implements MonoEffect {

    public static final String SAVE = "save";

    @Override
    public Stream<Target> targets(DeployedCard source) {
	return source.getPlayer().dyings().map(dc -> new Target(dc.getPosition()));
    }

    @Override
    public void apply(DeployedCard source, DeployedCard target) {
	target.setProtection(true);

    }

}
