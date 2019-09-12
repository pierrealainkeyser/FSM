package fr.keyser.evolutions;

import java.util.Set;

public final class FeedingSummary {
    private final int food;

    private final Set<FoodOrigin> origins;

    private final SpeciesId species;

    public FeedingSummary(SpeciesId species, int food, Set<FoodOrigin> origins) {
	this.species = species;
	this.food = food;
	this.origins = origins;
    }

    public int getFood() {
	return food;
    }

    public SpeciesId getSpecies() {
	return species;
    }

    public final Set<FoodOrigin> getOrigins() {
	return origins;
    }
}