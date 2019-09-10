package fr.keyser.evolutions;

public final class PlayerId {
    private final int id;

    public PlayerId(int id) {
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
	PlayerId other = (PlayerId) obj;
	if (id != other.id)
	    return false;
	return true;
    }

    public int getId() {
	return id;
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