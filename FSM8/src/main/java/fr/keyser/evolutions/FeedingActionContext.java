package fr.keyser.evolutions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class FeedingActionContext {
    private final Map<SpeciesId, SpeciesFeedingContext> feedingContext = new HashMap<>();

    private final Game game;

    public FeedingActionContext(Game game) {
	this.game = game;
    }

    private SpeciesFeedingContext createCtx(SpeciesId uid) {
	Player player = game.getPlayer(uid.getPlayer().getId());
	Species species = player.getSpecies(uid);
	return new SpeciesFeedingContext(species, this);
    }

    FeedingActionContext duplicate() {
	FeedingActionContext fac = new FeedingActionContext(game);
	for (SpeciesFeedingContext spec : feedingContext.values()) {
	    Species species = spec.getSpecies();
	    SpeciesFeedingContext dup = spec.duplicate(this);
	    fac.feedingContext.put(species.getUid(), dup);
	}

	return fac;
    }

    void feedRight(Species current, int quantity, FoodSource source) {
	Optional<Species> right = game.getPlayer(current).findRight(current);
	if (right.isPresent()) {
	    get(right.get()).feed(quantity, source);
	}
    }

    public SpeciesFeedingContext get(Species species) {
	return feedingContext.computeIfAbsent(species.getUid(), this::createCtx);
    }

    public List<FeedingSummary> summary() {
	return feedingContext.values().stream()
	        .map(SpeciesFeedingContext::summary)
	        .collect(Collectors.toList());
    }
}