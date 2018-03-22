package fr.keyser.pt;

import java.util.List;

public class TargetedEffectDescription {

    private final String name;

    private final List<CardPosition> target;

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
