package fr.keyser.evolutions;

import java.util.List;

public class FeedingAttackOperation extends AttackSummary implements FeedingOperation {

    private final List<FeedingSummary> feedings;

    private final SpeciesId species;

    public FeedingAttackOperation(SpeciesId species, AttackSummary attack, List<FeedingSummary> feedings) {
	super(attack.getAnalysis(), attack.getPopulationLoss());
	this.species = species;
	this.feedings = feedings;
    }

    @Override
    public String getType() {
	return "ATTACK";
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
