package fr.keyser.pt.fsm;

import java.util.List;

import fr.keyser.pt.DoDeployCard;

public class DoDeployCardCommand {
    private List<DoDeployCard> actions;

    private int keep;

    public DoDeployCardCommand() {
    }

    public DoDeployCardCommand(List<DoDeployCard> actions, int keep) {
	this.actions = actions;
	this.keep = keep;
    }

    public List<DoDeployCard> getActions() {
	return actions;
    }

    public int getKeep() {
	return keep;
    }

    public void setActions(List<DoDeployCard> actions) {
	this.actions = actions;
    }

    public void setKeep(int keep) {
	this.keep = keep;
    }
}