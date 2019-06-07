package fr.keyser.pt2;

import java.util.List;

public class PlayerMemento extends PlayerScoreMemento {
    private List<String> hand;

    private List<String> currentDraft;

    public List<String> getHand() {
	return hand;
    }

    public void setHand(List<String> hand) {
	this.hand = hand;
    }

    public List<String> getCurrentDraft() {
        return currentDraft;
    }

    public void setCurrentDraft(List<String> currentHand) {
        this.currentDraft = currentHand;
    }

}
