package fr.keyser.evolutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class Species {

    private final CardResolver cardResolver;

    private final int fatLevel;

    private final int foodLevel;

    private final int index;

    private final int population;

    private final int size;

    private final Map<Integer, CardId> traits;

    private final SpeciesId uid;

    public Species(CardResolver cardResolver, SpeciesId uid, int index) {
	this(cardResolver, uid, index, 1, 1, 0, 0, Collections.emptyMap());
    }

    private Species(CardResolver cardResolver, SpeciesId uid, int index, int population, int size, int foodLevel, int fatLevel,
            Map<Integer, CardId> traits) {
	this.cardResolver = cardResolver;
	this.uid = uid;
	this.index = index;
	this.population = population;
	this.size = size;
	this.foodLevel = foodLevel;
	this.fatLevel = fatLevel;
	this.traits = traits;
    }

    public TargetAttackAnalysis analyseAttack(Player player, TargetAttackContext ac) {
	return ac.analyse(this)
	        .filterCost(player.getHandSize());
    }

    public SpeciesView asView(Species publicSpecies, boolean replaceWithUnknow) {

	Optional<Species> pub = Optional.ofNullable(publicSpecies);
	Map<Integer, CardView> traits = new TreeMap<>();
	pub.ifPresent(s -> {
	    for (Entry<Integer, CardId> e : s.traits.entrySet()) {
		traits.put(e.getKey(), cardResolver.asView(e.getValue(), false));
	    }
	});

	for (Entry<Integer, CardId> e : this.traits.entrySet()) {
	    int key = e.getKey();

	    CardView existings = traits.get(key);
	    if (existings == null || !e.getValue().equals(existings.getCard())) {
		traits.put(key, cardResolver.asView(replaceWithUnknow ? CardId.UNKNOW : e.getValue(), true));
	    }
	}

	int fatlevel = pub.map(Species::getFatLevel).orElse(0);
	return new SpeciesView(uid, population, size, foodLevel, fatlevel,
	        new ArrayList<>(traits.values()));
    }

    public Species applyLoss(PopulationLossSummary summary) {
	if (summary.isExtinct())
	    return null;

	return new Species(cardResolver, uid, index, population - summary.getDelta(), size, foodLevel, fatLevel, traits);
    }

    public List<AttackSummary> attacksSummaries(Player player, CarnivorousContext ctx) {
	return ctx.getTargetAttackContexts().stream()
	        .filter(a -> !a.isSelf(this)) // can't attack self
	        .map(a -> summariseAttack(player, a))
	        .collect(Collectors.toList());
    }

    public List<FeedingAttackOperation> feedingAttackOperations(Player player, CarnivorousContext ctx) {
	return ctx.getTargetAttackContexts().stream()
	        .filter(a -> !a.isSelf(this)) // can't attack self
	        .map(a -> ctx.summary(this, a.getTarget(), summariseAttack(player, a)))
	        .collect(Collectors.toList());
    }

    public PopulationLossSummary checkPopulation() {
	return populationLoss(0);
    }

    private Species clearFat() {
	return new Species(cardResolver, uid, index, population, size, foodLevel, 0, traits);
    }

    private Set<Trait> computeTraits() {
	return traits.values().stream().map(id -> cardResolver.resolve(id).getTrait()).collect(Collectors.toSet());
    }

    public Species updateFertile() {
	if (hasTrait(Trait.FERTILE) && population < 6)
	    return new Species(cardResolver, uid, index, population + 1, size, foodLevel, fatLevel, traits);
	else
	    return this;
    }

    public Species evolve(EvolutionInstructions instruction) {
	int population = this.population + instruction.getPopulation().size();
	int size = this.size + instruction.getSize().size();

	Map<Integer, CardId> traits = new TreeMap<>(this.traits);
	traits.putAll(instruction.getTraits());

	Species newSpecies = new Species(cardResolver, uid, index, population, size, foodLevel, fatLevel, traits);
	if (hasTrait(Trait.FATTY) && !newSpecies.hasTrait(Trait.FATTY))
	    newSpecies = newSpecies.clearFat();

	return newSpecies;
    }

    public Species feed(FeedingSummary summary) {
	int food = summary.getFood();
	int footDelta = Math.min(food, getBaseFeedCapacity());
	int fatDelta = 0;

	if (hasTrait(Trait.FATTY))
	    fatDelta = Math.max(0, food - footDelta);

	return new Species(cardResolver, uid, index, population, size, foodLevel + footDelta, fatLevel + fatDelta, traits);
    }

    public Optional<CardId> findCardWithTrait(Trait t) {
	return traits.values().stream().filter(id -> cardResolver.resolve(id).getTrait().equals(t)).findFirst();
    }

    public int getBaseFeedCapacity() {
	return Math.max(0, population - foodLevel);
    }

    public int getFatLevel() {
	return fatLevel;
    }

    public int getFeedCapacity() {
	boolean fatty = hasTrait(Trait.FATTY);
	return getBaseFeedCapacity() + (fatty ? (size - fatLevel) : 0);
    }

    public int getFoodLevel() {
	return foodLevel;
    }

    public int getIndex() {
	return index;
    }

    public int getPopulation() {
	return population;
    }

    public int getSize() {
	return size;
    }

    public SpeciesId getUid() {
	return uid;
    }

    public boolean hasTrait(Trait trait) {
	return computeTraits().contains(trait);
    }

    public HungerStatus hunger() {
	if (foodLevel < population)
	    return HungerStatus.HUNGRY;
	else if (hasTrait(Trait.FATTY) && fatLevel < size)
	    return HungerStatus.MAY_EAT_MORE;
	else
	    return HungerStatus.FULL;
    }

    public boolean isSame(Species species) {
	return uid.equals(species.uid);
    }

    public boolean mayEat(Player owner, Game game) {
	if (hunger() == HungerStatus.FULL)
	    return false;

	if (hasTrait(Trait.CARNIVOROROUS)) {
	    return game.hasPreyFor(owner, this);
	} else {
	    return game.getFoodPool() > 0;
	}
    }

    public PopulationLossSummary populationLoss(int delta) {
	return new PopulationLossSummary(delta, population + delta <= 0, traits.size(), foodLevel + fatLevel);
    }

    public Species resetPopulationFood() {
	return new Species(cardResolver, uid, index, foodLevel, size, 0, fatLevel, traits);
    }

    private AttackSummary summariseAttack(Player player, TargetAttackContext ac) {
	TargetAttackAnalysis analysis = analyseAttack(player, ac);

	OnAttackPopulationLoss results = null;

	if (analysis.isPossible()) {
	    CostAnalysis evitableDamage = null;
	    PopulationLossSummary attackerDamage = null;
	    Species target = ac.getTarget();
	    boolean horned = target.hasTrait(Trait.HORNED);
	    if (horned) {
		attackerDamage = populationLoss(-1);
		Optional<CardId> intelligent = findCardWithTrait(Trait.INTELIGGENT);
		if (intelligent.isPresent()) {

		    evitableDamage = CostAnalysis.conditional(Collections.singletonMap(intelligent.get(), 1));
		    int totalCost = analysis.getTotalCost() + 1;
		    if (player.getHandSize() < totalCost)
			evitableDamage = evitableDamage.tooCostly();

		} else
		    evitableDamage = CostAnalysis.imposible();

	    }
	    results = new OnAttackPopulationLoss(attackerDamage, evitableDamage,
	            target.populationLoss(-1));

	}
	return new AttackSummary(analysis, results);
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Species [uid=").append(uid)
	        .append(", index=").append(index)
	        .append(", population=").append(population)
	        .append(", size=").append(size)
	        .append(", foodLevel=").append(foodLevel)
	        .append(", fatLevel=").append(fatLevel)
	        .append(", traits=")
	        .append(asTrait(traits))
	        .append("]");
	return builder.toString();
    }

    private List<Trait> asTrait(Map<Integer, CardId> traits) {
	return traits.values().stream().map(c -> cardResolver.resolve(c).getTrait()).collect(Collectors.toList());
    }

    public Species transfertFat() {
	int transfert = Math.min(getBaseFeedCapacity(), fatLevel);

	return new Species(cardResolver, uid, index, population, size, foodLevel + transfert, fatLevel - transfert, traits);
    }
}