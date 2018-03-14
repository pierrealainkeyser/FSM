package fr.keyser.pt;

public final class CardPosition {
    public static enum Position {
	BACK, BUILDING, FRONT
    }

    private final int index;

    private final Position position;

    public CardPosition(Position position, int index) {
	this.position = position;
	this.index = index;
    }

    public boolean isOnFrontLine() {
	return Position.FRONT == getPosition();
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

}
