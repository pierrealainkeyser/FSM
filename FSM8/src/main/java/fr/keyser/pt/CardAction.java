package fr.keyser.pt;

public class CardAction {

    private CardPosition source;

    private CardPosition target;

    public CardAction() {
    }

    public CardAction(CardPosition source, CardPosition target) {
	this.source = source;
	this.target = target;
    }

    public CardPosition getSource() {
	return source;
    }

    public CardPosition getTarget() {
	return target;
    }

    public void setSource(CardPosition source) {
	this.source = source;
    }

    public void setTarget(CardPosition target) {
	this.target = target;
    }

}
