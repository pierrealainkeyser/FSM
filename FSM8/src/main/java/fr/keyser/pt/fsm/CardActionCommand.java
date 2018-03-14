package fr.keyser.pt.fsm;

import java.util.List;

import fr.keyser.pt.CardAction;

public class CardActionCommand {
    private List<CardAction> actions;

    public CardActionCommand() {
    }

    public CardActionCommand(List<CardAction> actions) {
	this.actions = actions;
    }

    public List<CardAction> getActions() {
	return actions;
    }

    public void setActions(List<CardAction> actions) {
	this.actions = actions;
    }
}