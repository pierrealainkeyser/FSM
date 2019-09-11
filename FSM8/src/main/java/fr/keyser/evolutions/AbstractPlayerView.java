package fr.keyser.evolutions;

import java.util.List;

public abstract class AbstractPlayerView {

    private final PlayerSpeciesView species;

    private final PlayerStatus status;

    protected AbstractPlayerView(PlayerSpeciesView species, PlayerStatus status) {
	this.species = species;
	this.status = status;
    }

    public final List<SpeciesView> getSpecies() {
	return species.getSpecies();
    }

    public final PlayerStatus getStatus() {
	return status;
    }
}
