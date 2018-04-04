package fr.keyser.pt;

public class DeployedCardInfo {

    private CardModel model;

    private CardPosition position;

    public DeployedCardInfo() {
    }

    public DeployedCardInfo(CardPosition position, CardModel model) {
	this.position = position;
	this.model = model;
    }

    public CardModel getModel() {
	return model;
    }

    public CardPosition getPosition() {
	return position;
    }

    public void setModel(CardModel model) {
	this.model = model;
    }

    public void setPosition(CardPosition position) {
	this.position = position;
    }

}
