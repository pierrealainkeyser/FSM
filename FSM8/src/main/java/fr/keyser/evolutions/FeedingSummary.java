package fr.keyser.evolutions;

import java.util.List;

public final class FeedingSummary {
    private final int food;

    private final List<FoodSource> sources;

    private final SpeciesId species;

    public FeedingSummary(SpeciesId species, int food, List<FoodSource> sources) {
	this.species = species;
	this.food = food;
	this.sources = sources;
    }

    public int getFood() {
	return food;
    }

    public List<FoodSource> getSources() {
	return sources;
    }

    public SpeciesId getSpecies() {
	return species;
    }
}