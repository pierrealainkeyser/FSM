package fr.keyser.evolutions;

public final class SpeciesId {
    public static int compare(SpeciesId l, SpeciesId r) {
	return l.id - r.id;
    }

    private final int id;

    private final PlayerId player;

    public SpeciesId(PlayerId player, int id) {
	this.player = player;
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
	SpeciesId other = (SpeciesId) obj;
	if (id != other.id)
	    return false;
	if (player == null) {
	    if (other.player != null)
		return false;
	} else if (!player.equals(other.player))
	    return false;
	return true;
    }

    public PlayerId getPlayer() {
	return player;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + id;
	result = prime * result + ((player == null) ? 0 : player.hashCode());
	return result;
    }

    public SpeciesId next() {
	return new SpeciesId(player, id + 1);
    }

    @Override
    public String toString() {
	return player + "#" + id;
    }

}