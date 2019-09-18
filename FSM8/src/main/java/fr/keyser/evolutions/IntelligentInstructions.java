package fr.keyser.evolutions;

import java.util.HashSet;
import java.util.Set;

public final class IntelligentInstructions {

    private SpeciesId species;

    private Set<CardId> discardeds = new HashSet<>();

    public SpeciesId getSpecies() {
	return species;
    }

    public void setSpecies(SpeciesId species) {
	this.species = species;
    }

    public Set<CardId> getDiscardeds() {
	return discardeds;
    }

    public void setDiscardeds(Set<CardId> discardeds) {
	this.discardeds = discardeds;
    }

}
