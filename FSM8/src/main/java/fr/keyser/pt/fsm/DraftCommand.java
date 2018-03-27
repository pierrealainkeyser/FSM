package fr.keyser.pt.fsm;

import fr.keyser.pt.MetaCard;

public class DraftCommand {

    private Integer discard;

    private int draft;

    public DraftCommand() {
    }

    public DraftCommand(int draft) {
	this(draft, null);
    }

    public DraftCommand(MetaCard draft) {
	this(draft.getId(), null);
    }

    public DraftCommand(int draft, Integer discard) {
	this.draft = draft;
	this.discard = discard;
    }

    public DraftCommand(MetaCard draft, MetaCard discard) {
	this(draft.getId(), discard.getId());
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
