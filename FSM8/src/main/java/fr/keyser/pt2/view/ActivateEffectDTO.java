package fr.keyser.pt2.view;

import java.util.Map;

import fr.keyser.pt.CardPosition;

public class ActivateEffectDTO {

    private CardPosition source;

    private Map<String, CardPosition> targets;

    public CardPosition getSource() {
	return source;
    }

    public void setSource(CardPosition source) {
	this.source = source;
    }

    public Map<String, CardPosition> getTargets() {
	return targets;
    }

    public void setTargets(Map<String, CardPosition> targets) {
	this.targets = targets;
    }

}
