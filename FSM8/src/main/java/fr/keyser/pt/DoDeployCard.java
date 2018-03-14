package fr.keyser.pt;

public class DoDeployCard {

    private int source;

    private CardPosition target;

    public DoDeployCard() {
    }

    public DoDeployCard(int source, CardPosition target) {
	this.source = source;
	this.target = target;
    }

    public int getSource() {
        return source;
    }

    public CardPosition getTarget() {
        return target;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public void setTarget(CardPosition target) {
        this.target = target;
    }
}
