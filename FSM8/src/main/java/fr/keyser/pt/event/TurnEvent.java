package fr.keyser.pt.event;

public class TurnEvent {

    private final int turn;

    public TurnEvent(int turn) {
	this.turn = turn;
    }

    public int getTurn() {
	return turn;
    }
}
