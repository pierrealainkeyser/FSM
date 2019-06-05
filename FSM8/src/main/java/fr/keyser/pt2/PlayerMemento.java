package fr.keyser.pt2;

import java.util.List;

public class PlayerMemento extends PlayerScoreMemento {
    private List<String> hand;

    private List<String> currentHand;

    public List<String> getHand() {
	return hand;
    }

    public void setHand(List<String> hand) {
	this.hand = hand;
    }

    public List<String> getCurrentHand() {
        return currentHand;
    }

    public void setCurrentHand(List<String> currentHand) {
        this.currentHand = currentHand;
    }

}
