package fr.keyser.pt2.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.keyser.pt.CardPosition;

public class Target {

    public static final String DEFAULT = "_";

    private final String name;

    private final CardPosition position;

    private List<Target> targets;

    public Target(CardPosition position) {
	this(DEFAULT, position);
    }

    public Target(String name, CardPosition position) {
	this.name = name;
	this.position = position;
    }

    public void add(Target target) {
	if (targets == null)
	    targets = new ArrayList<>();
	targets.add(target);
    }

    public String getName() {
	return name;
    }

    public CardPosition getPosition() {
	return position;
    }

    public List<Target> getTargets() {
	if (targets == null)
	    return Collections.emptyList();
	return Collections.unmodifiableList(targets);
    }

}
