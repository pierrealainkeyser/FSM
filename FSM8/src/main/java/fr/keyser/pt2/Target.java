package fr.keyser.pt2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.keyser.pt.CardPosition;

public class Target {

    private final String name;

    private final CardPosition position;

    private List<Target> targets;

    Target(String name, CardPosition position) {
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

    @Override
    public String toString() {
	return "Target [name=" + name + ", position=" + position + ", targets=" + targets + "]";
    }

}
