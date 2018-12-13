package fr.keyser.pt;

public final class CardPosition {
    public static enum Position {
	BACK, BUILDING, FRONT;

	public CardPosition index(int index) {
	    return new CardPosition(this, index);
	}
    }

    private final int index;

    private final Position position;

    private CardPosition(Position position, int index) {
	this.position = position;
	this.index = index;
    }

    public boolean isUnit() {
	return Position.FRONT == position || position == Position.BACK;
    }

    public static boolean mayCombat(CardPosition cp) {
	if (cp == null)
	    return false;
	else
	    return Position.FRONT == cp.position || Position.BUILDING == cp.position;
    }

    public int getIndex() {
	return index;
    }

    public Position getPosition() {
	return position;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + index;
	result = prime * result + ((position == null) ? 0 : position.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	CardPosition other = (CardPosition) obj;
	if (index != other.index)
	    return false;
	if (position != other.position)
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "CardPosition [position=" + position + ", index=" + index + "]";
    }

}
