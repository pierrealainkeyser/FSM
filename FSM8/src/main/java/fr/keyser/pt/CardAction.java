package fr.keyser.pt;

import java.util.Collections;
import java.util.Map;

public class CardAction {

    private CardPosition source;

    private Map<String, CardPosition> target;

    public CardAction() {
    }

    public CardAction(CardPosition source, String name, CardPosition target) {
	this(source, Collections.singletonMap(name, target));
    }

    public CardAction(CardPosition source, Map<String, CardPosition> target) {
	this.source = source;
	this.target = target;
    }

    public CardPosition getSource() {
	return source;
    }

    public Map<String, CardPosition> getTarget() {
	return target;
    }

    public void setSource(CardPosition source) {
	this.source = source;
    }

    public void setTarget(Map<String, CardPosition> target) {
	this.target = target;
    }

}
