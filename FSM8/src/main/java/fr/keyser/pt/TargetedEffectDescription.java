package fr.keyser.pt;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TargetedEffectDescription {

    private final String name;

    private final List<CardPosition> target;

    public TargetedEffectDescription(String name, Stream<DeployedCard> target) {
	this(name, target.map(DeployedCard::getPosition).collect(Collectors.toList()));
    }

    public TargetedEffectDescription(String name, List<CardPosition> target) {
	this.name = name;
	this.target = target;
    }

    public String getName() {
	return name;
    }

    public List<CardPosition> getTarget() {
	return target;
    }

}
