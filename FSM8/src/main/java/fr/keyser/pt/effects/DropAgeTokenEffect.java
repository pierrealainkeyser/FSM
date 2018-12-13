package fr.keyser.pt.effects;

import java.util.stream.Stream;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;

public class DropAgeTokenEffect implements MonoEffect {

    public static final String DROP_AGE = "dropAge";
    private final IntValue ageCount;

    public DropAgeTokenEffect(IntValue ageCount) {
	this.ageCount = ageCount;
    }

    @Override
    public Stream<Target> targets(DeployedCard source) {
	return source.getPlayer().units().map(dc -> new Target(dc.getPosition()));
    }

    @Override
    public void apply(DeployedCard source, DeployedCard target) {
	int age = ageCount.getValue(source);
	target.doAge(age);

    }

}
