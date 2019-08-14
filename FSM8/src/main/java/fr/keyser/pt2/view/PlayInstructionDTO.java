package fr.keyser.pt2.view;

import java.util.List;

import fr.keyser.pt.CardPosition;

public class PlayInstructionDTO {

    private static class ToDTO {
	private CardPosition to;

	public CardPosition getTo() {
	    return to;
	}

	public void setTo(CardPosition to) {
	    this.to = to;
	}
    }

    public static class MoveDTO extends ToDTO {
	private CardPosition from;

	public CardPosition getFrom() {
	    return from;
	}

	public void setFrom(CardPosition from) {
	    this.from = from;
	}

    }

    public static class DeployDTO extends ToDTO {
	private int card;

	public int getCard() {
	    return card;
	}

	public void setCard(int card) {
	    this.card = card;
	}

    }

    private List<MoveDTO> moves;

    private List<DeployDTO> deploys;

    public List<MoveDTO> getMoves() {
	return moves;
    }

    public void setMoves(List<MoveDTO> moves) {
	this.moves = moves;
    }

    public List<DeployDTO> getDeploys() {
	return deploys;
    }

    public void setDeploys(List<DeployDTO> deploys) {
	this.deploys = deploys;
    }

}
