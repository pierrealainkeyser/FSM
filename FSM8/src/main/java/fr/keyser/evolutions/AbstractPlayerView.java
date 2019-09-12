package fr.keyser.evolutions;

import java.util.List;

public abstract class AbstractPlayerView {

    private final PlayerSpeciesView species;

    private final PlayerStatus status;

    private final int index;

    protected AbstractPlayerView(int index, PlayerSpeciesView species, PlayerStatus status) {
	this.index = index;
	this.species = species;
	this.status = status;
    }

    public final List<SpeciesView> getSpecies() {
	return species.getSpecies();
    }

    public final PlayerStatus getStatus() {
	return status;
    }

    public final int getIndex() {
	return index;
    }
}
