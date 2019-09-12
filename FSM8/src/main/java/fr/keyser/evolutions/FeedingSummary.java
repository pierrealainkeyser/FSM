package fr.keyser.evolutions;

public final class FeedingSummary {
    private final int food;

    private final SpeciesId species;

    public FeedingSummary(SpeciesId species, int food) {
	this.species = species;
	this.food = food;
    }

    public int getFood() {
	return food;
    }

    public SpeciesId getSpecies() {
	return species;
    }
}