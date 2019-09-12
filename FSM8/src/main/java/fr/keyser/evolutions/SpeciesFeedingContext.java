package fr.keyser.evolutions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SpeciesFeedingContext {

    private int capacity;

    private final FeedingActionContext context;

    private int feed;

    private final List<FoodSource> sources;

    private final Species species;

    public SpeciesFeedingContext(Species species, FeedingActionContext context) {
	this(species, species.getFeedCapacity(), 0, context, new ArrayList<>());
    }

    private SpeciesFeedingContext(Species species, int capacity, int feed, FeedingActionContext context,
            List<FoodSource> sources) {
	this.capacity = capacity;
	this.feed = feed;
	this.context = context;
	this.sources = sources;
	this.species = species;
    }

    SpeciesFeedingContext duplicate(FeedingActionContext context) {
	return new SpeciesFeedingContext(species, capacity, feed, context, new ArrayList<>(sources));
    }

    void feed(int quantity, FoodSource source) {
	int remaining = Math.max(0, capacity - feed);

	if (FoodType.PLANT == source.getFoodType()) {
	    if (species.hasTrait(Trait.CARNIVOROROUS)) {
		return;
	    } else if (species.hasTrait(Trait.FORAGING)) {
		++quantity;
	    }
	}

	int tryConsuming = Math.min(remaining, quantity);
	int consume = source.consume(tryConsuming);

	if (consume > 0) {
	    this.feed += consume;
	    sources.add(source);

	    Optional<CardId> collaborative = species.findCardWithTrait(Trait.COLLABORATIVE);
	    if (collaborative.isPresent()) {
		context.feedRight(species, 1, source.traitBased(collaborative.get()));
	    }
	}

    }

    public void feedAttack(Species target) {
	feed(target.getPopulation(), UnlimitedFoodSource.MEAT);
    }

    public void feedLongNeck() {
	feed(1, UnlimitedFoodSource.PLANT);
    }

    public void feedScavenger() {
	feed(1, UnlimitedFoodSource.MEAT);
    }

    public void feedWateringHole(WateringHole wateringHole) {
	feed(1, wateringHole);
    }

    public Species getSpecies() {
	return species;
    }

    public void reduceCapacity(PopulationLossSummary loss) {
	this.capacity = Math.max(0, capacity + loss.getDelta());
    }

    public boolean hasEated() {
	return feed > 0;
    }

    public FeedingSummary summary() {
	return new FeedingSummary(species.getUid(), feed);
    }

}