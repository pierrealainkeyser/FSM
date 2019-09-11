package fr.keyser.evolutions;

import java.util.List;

public class PlayerSpeciesView {
    private final List<SpeciesView> species;

    public PlayerSpeciesView(List<SpeciesView> species) {
	this.species = species;
    }

    public List<SpeciesView> getSpecies() {
        return species;
    }

}
