package fr.keyser.evolutions;

import java.util.List;

public class FeedingWateringHoleOperation implements FeedingOperation {

    private final List<FeedingSummary> feedings;

    private final SpeciesId species;

    public FeedingWateringHoleOperation(SpeciesId species, List<FeedingSummary> feedings) {
	this.species = species;
	this.feedings = feedings;
    }

    @Override
    public String getType() {
	return "PLANT";
    }

    @Override
    public List<FeedingSummary> getFeedings() {
	return feedings;
    }

    @Override
    public SpeciesId getSpecies() {
	return species;
    }

}
