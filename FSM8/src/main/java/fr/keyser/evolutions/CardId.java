package fr.keyser.evolutions;

public final class CardId {

    public final static CardId UNKNOW = new CardId(-1);

    private final int id;

    public CardId(int id) {
	this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	CardId other = (CardId) obj;
	if (id != other.id)
	    return false;
	return true;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + id;
	return result;
    }

    @Override
    public String toString() {
	return id + "";
    }
}