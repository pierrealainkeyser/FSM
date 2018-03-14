package fr.keyser.pt.fsm;

public class DraftCommand {

    private Integer discard;

    private int draft;

    public DraftCommand() {
    }

    public DraftCommand(int draft, int discard) {
	this.draft = draft;
	this.discard = discard;
    }

    public Integer getDiscard() {
	return discard;
    }

    public int getDraft() {
	return draft;
    }

    public void setDiscard(Integer discard) {
	this.discard = discard;
    }

    public void setDraft(int draft) {
	this.draft = draft;
    }
}
