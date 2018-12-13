package fr.keyser.pt;

import java.util.Collections;
import java.util.Map;

public class CardAction {

    private CardPosition source;

    private int index;

    private Map<String, CardPosition> target;

    public CardAction() {
    }

    public CardAction(CardPosition source, int index, String name, CardPosition target) {
	this(source, index, Collections.singletonMap(name, target));
    }

    public CardAction(CardPosition source, int index, Map<String, CardPosition> target) {
	this.source = source;
	this.index = index;
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

    public int getIndex() {
	return index;
    }

    public void setIndex(int index) {
	this.index = index;
    }

}
