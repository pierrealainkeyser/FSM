package fr.keyser.evolutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Player {

    private final CardResolver cardResolver;

    private final int foodEated;

    private final CardId foodPlayed;

    private final PlayerId index;

    private final List<CardId> inHands;

    private final NavigableMap<Integer, Species> species;

    public Player(CardResolver cardResolver, int index) {
	this(cardResolver, new PlayerId(index), 0,
	        new TreeMap<>(Collections.singletonMap(0, new Species(cardResolver, new SpeciesId(new PlayerId(index), 0), 0))), null,
	        Collections.emptyList());
    }

    private Player(CardResolver cardResolver, PlayerId index, int foodEated, NavigableMap<Integer, Species> species, CardId foodPlayed,
            List<CardId> inHands) {
	this.cardResolver = cardResolver;
	this.index = index;
	this.foodEated = foodEated;
	this.species = Collections.unmodifiableNavigableMap(species);
	this.foodPlayed = foodPlayed;
	this.inHands = Collections.unmodifiableList(inHands);
    }

    public Player discard(AttackInstructions instruction) {
	List<CardId> inHands = new ArrayList<>(this.inHands);
	inHands.removeAll(instruction.getDamageReduction());
	inHands.removeAll(instruction.getTraitAvoidances());
	return new Player(cardResolver, index, foodEated, species, foodPlayed, inHands);
    }

    public List<CardView> handsAsView() {
	return inHands.stream().map(id -> cardResolver.asView(id, true)).collect(Collectors.toList());
    }

    public TargetAttackContext attackForSpecies(Species target) {
	return new TargetAttackContext(findNext(target, false).orElse(null), target, findRight(target).orElse(null));
    }

    private Player changeAllSpecies(UnaryOperator<Species> operator) {
	NavigableMap<Integer, Species> news = new TreeMap<>(species);
	for (Entry<Integer, Species> e : news.entrySet()) {
	    e.setValue(operator.apply(e.getValue()));
	}
	return new Player(cardResolver, index, foodEated, news, foodPlayed, inHands);
    }

    public Player clearExtinctsSpecies(List<CardId> draws) {

	NavigableMap<Integer, Species> newSpecies = new TreeMap<>();
	for (Species species : this.species.values()) {
	    if (!species.checkPopulation().isExtinct()) {
		newSpecies.put(species.getIndex(), species);
	    }
	}

	List<CardId> inHands = new ArrayList<>(this.inHands);
	inHands.addAll(draws);

	return new Player(cardResolver, index, foodEated, newSpecies, foodPlayed, inHands);
    }

    public Player clearFoodPlayed() {
	return new Player(cardResolver, index, foodEated, species, null, inHands);
    }

    public Player doScore() {

	int scored = 0;
	NavigableMap<Integer, Species> newSpecies = new TreeMap<>();
	for (Species species : this.species.values()) {
	    scored += species.getFoodLevel();
	    newSpecies.put(species.getIndex(), species.resetPopulationFood());
	}

	return new Player(cardResolver, index, foodEated + scored, newSpecies, foodPlayed, inHands);
    }

    public List<FeedingOperation> computeFeedingOperations(Game game) {
	CarnivorousContext carnivorous = game.carnivorousContext();

	return species.values().stream().flatMap(s -> {
	    if (s.hunger() != HungerStatus.FULL) {
		if (s.hasTrait(Trait.CARNIVOROROUS)) {
		    return s.feedingAttackOperations(this, carnivorous).stream();
		} else {
		    int foodPool = game.getFoodPool();
		    if (foodPool > 0) {
			FeedingActionContext fac = new FeedingActionContext(game);
			fac.get(s).feedWateringHole(new WateringHole(foodPool));
			return Stream.of(new FeedingWateringHoleOperation(s.getUid(), fac.summary()));
		    }
		}
	    }

	    return Stream.empty();
	}).collect(Collectors.toList());
    }

    public Player draws(List<CardId> cards) {

	List<CardId> inHands = new ArrayList<>(this.inHands);
	inHands.addAll(cards);

	return new Player(cardResolver, index, foodEated, species, foodPlayed, inHands);
    }

    public Player evolve(EvolutionInstructions instruction) {
	int index = instruction.getIndex();
	NavigableMap<Integer, Species> news = new TreeMap<>(species);

	List<CardId> inHands = new ArrayList<>(this.inHands);
	inHands.removeAll(instruction.getNewSpecies());
	inHands.removeAll(instruction.getPopulation());
	inHands.removeAll(instruction.getSize());
	inHands.removeAll(instruction.getTraits().values());

	int scoreFat = 0;
	Species after = null;
	if (news.containsKey(index)) {
	    Species before = news.get(index);
	    after = before.evolve(instruction);

	    // remove
	    if (before.hasTrait(Trait.FATTY) && !after.hasTrait(Trait.FATTY))
		scoreFat = before.getFatLevel();

	} else {
	    after = new Species(cardResolver, nextId(), index)
	            .evolve(instruction);

	}
	news.put(index, after);

	return new Player(cardResolver, this.index, foodEated + scoreFat, news, foodPlayed, inHands);
    }

    public Player feed(FeedingSummary summary) {
	return updateSpecies(summary.getSpecies(), s -> s.feed(summary));
    }

    private Optional<Species> findNext(Species species, boolean higher) {
	int speciesKey = species.getIndex();
	Integer key = higher ? this.species.higherKey(speciesKey) : this.species.lowerKey(speciesKey);
	return Optional.ofNullable(findSpecs(key));
    }

    public Optional<Species> findRight(Species species) {
	return findNext(species, true);
    }

    private Species findSpecs(Integer key) {
	if (key == null)
	    return null;
	else
	    return species.get(key);
    }

    public int getExtinctionCardsToDraw() {
	return species.values().stream()
	        .map(Species::checkPopulation)
	        .filter(PopulationLossSummary::isExtinct)
	        .map(PopulationLossSummary::getDelta)
	        .reduce(0, (l, r) -> l + r);
    }

    public int getFoodEated() {
	return foodEated;
    }

    public Stream<Integer> getFoodPlayed() {
	if (foodPlayed == null)
	    return Stream.empty();
	else
	    return Stream.of(cardResolver.resolve(foodPlayed).getFood());
    }

    public int getHandSize() {
	return inHands.size();
    }

    public PlayerId getIndex() {
	return index;
    }

    public List<CardId> getInHands() {
	return inHands;
    }

    public Species getSpecies(SpeciesId id) {
	return species.values().stream().filter(s -> s.getUid().equals(id)).findFirst().orElse(null);
    }

    public int getSpeciesCount() {
	return species.size();
    }

    public Stream<Species> longNecks() {
	return species.values().stream().filter(s -> s.hasTrait(Trait.LONG_NECK));
    }

    public boolean mayEat(Game game) {
	return species.values().stream().anyMatch(s -> s.mayEat(this, game));
    }

    private SpeciesId nextId() {
	Optional<SpeciesId> max = species.values().stream().map(Species::getUid).max(SpeciesId::compare);
	return max.map(SpeciesId::next).orElse(new SpeciesId(index, 0));
    }

    public Player playFood(CardId foodPlayed) {
	List<CardId> inHands = new ArrayList<>(this.inHands);
	inHands.remove(foodPlayed);

	return new Player(cardResolver, index, foodEated, species, foodPlayed, inHands);
    }

    public Player populationLoss(SpeciesId species, PopulationLossSummary loss) {
	return updateSpecies(species, s -> s.applyLoss(loss));
    }

    public Stream<Species> scavengers() {
	return species.values().stream().filter(s -> s.hasTrait(Trait.SCAVENGER));
    }

    public Stream<TargetAttackContext> targetAttackContexts() {
	return species.values().stream().map(this::attackForSpecies);
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Player [index=").append(index).append(", inHands=")
	        .append(inHands.stream().map(cardResolver::resolve).collect(Collectors.toList())).append(", species=")
	        .append(species.values())
	        .append(", foodEated=").append(foodEated).append(", foodPlayed=")
	        .append(foodPlayed == null ? null : cardResolver.resolve(foodPlayed)).append("]");
	return builder.toString();
    }

    public PlayerSpeciesView asView(Player publicPlayer, boolean replaceWithUnknow) {
	List<SpeciesView> views = new ArrayList<>(species.size());
	for (Entry<Integer, Species> e : species.entrySet()) {
	    int index = e.getKey();
	    Species privateSpecies = e.getValue();
	    Species publicSpecies = publicPlayer.species.get(index);
	    views.add(privateSpecies.asView(publicSpecies, replaceWithUnknow));
	}
	return new PlayerSpeciesView(views);
    }

    public Player transfertFat() {
	return changeAllSpecies(Species::transfertFat);
    }

    public Player updateFertile() {
	return changeAllSpecies(Species::updateFertile);
    }

    private Player updateSpecies(SpeciesId id, Function<Species, Species> functor) {
	NavigableMap<Integer, Species> news = new TreeMap<>(species);

	int deltaFood = 0;

	Iterator<Entry<Integer, Species>> it = news.entrySet().iterator();
	while (it.hasNext()) {
	    Entry<Integer, Species> e = it.next();
	    Species src = e.getValue();
	    if (src.getUid().equals(id)) {
		Species result = functor.apply(src);
		if (result == null) {
		    it.remove();
		    deltaFood = src.getFoodLevel() + src.getFatLevel();
		} else
		    e.setValue(result);
		break;
	    }
	}

	return new Player(cardResolver, index, foodEated + deltaFood, news, foodPlayed, inHands);
    }

}