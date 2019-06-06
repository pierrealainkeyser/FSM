package fr.keyser.pt2.view;

public class PickInstructionDTO {

    private int pick;

    public int getPick() {
	return pick;
    }

    public void setPick(int pick) {
	this.pick = pick;
    }

    @Override
    public String toString() {
	return "[pick=" + pick + "]";
    }
}
