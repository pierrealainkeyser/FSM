package fr.keyser.pt2.effects;

import java.util.List;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt2.Target;

public class CardTargets {

    private CardPosition source;

    private List<Target> targets;

    public CardTargets(CardPosition source, List<Target> targets) {
	this.source = source;
	this.targets = targets;
    }

    public CardPosition getSource() {
	return source;
    }

    public List<Target> getTargets() {
	return targets;
    }

}
