package fr.keyser.evolutions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private final Map<Integer, CardId> privateTraits;

    private final SpeciesId uid;

    public Species(CardResolver cardResolver, SpeciesId uid, int index) {
	this(cardResolver, uid, index, 1, 1, 0, 0, Collections.emptyMap(), Collections.emptyMap());
    }

    private Species(CardResolver cardResolver, SpeciesId uid, int index, int population, int size, int foodLevel, int fatLevel,
            Map<Integer, CardId> traits, Map<Integer, CardId> privateTraits) {
	this.cardResolver = cardResolver;
	this.uid = uid;
	this.index = index;
	this.population = population;
	this.size = size;
	this.foodLevel = foodLevel;
	this.fatLevel = fatLevel;
	this.traits = traits;
	this.privateTraits = privateTraits;
    }

    public TargetAttackAnalysis analyseAttack(Player player, TargetAttackContext ac) {
	return ac.analyse(this)
	        .filterCost(player.getHandSize());
    }

    public Species applyLoss(PopulationLossSummary summary) {
	if (summary.isExtinct())
	    return null;

	return new Species(cardResolver, uid, index, population - summary.getPopulationLoss(), size, foodLevel, fatLevel, traits,
	        privateTraits);
    }

    List<AttackSummary> attacksSummaries(Player player, CarnivorousContext ctx) {
	return attacksSummaries(player, ctx.getTargetAttackContexts());
    }

    public List<AttackSummary> attacksSummaries(Player player, List<TargetAttackContext> targetAttackContexts) {
	return targetAttackContexts.stream()
	        .filter(a -> !a.isSelf(this)) // can't attack self
	        .map(a -> summariseAttack(player, a))
	        .collect(Collectors.toList());

    }

    public PopulationLossSummary checkPopulation() {
	return populationLoss(0);
    }

    private Species clearFat() {
	return new Species(cardResolver, uid, index, population, size, foodLevel, 0, traits, privateTraits);
    }

    private Set<Trait> computeTraits(Map<Integer, CardId> traits) {
	return traits.values().stream().map(id -> cardResolver.resolve(id).getTrait()).collect(Collectors.toSet());
    }

    public Species updateFertile() {
	if (hasTrait(Trait.FERTILE) && population < 6)
	    return new Species(cardResolver, uid, index, population + 1, size, foodLevel, fatLevel, traits, privateTraits);
	else
	    return this;
    }

    public Species evolve(EvolutionInstructions instruction) {
	int population = this.population + instruction.getPopulation().size();
	int size = this.size + instruction.getSize().size();

	Map<Integer, CardId> privateTraits = new TreeMap<>(this.traits);
	privateTraits.putAll(instruction.getTraits());

	Species newSpecies = new Species(cardResolver, uid, index, population, size, foodLevel, fatLevel, traits, privateTraits);
	if (hasTrait(Trait.FATTY) && !newSpecies.hasPrivateTrait(Trait.FATTY))
	    newSpecies = newSpecies.clearFat();

	return newSpecies;
    }

    public Species publishTraits() {
	return new Species(cardResolver, uid, index, population, size, foodLevel, fatLevel, privateTraits, Collections.emptyMap());
    }

    public Species feed(FeedingSummary summary) {
	int food = summary.getFood();
	int footDelta = Math.min(food, getBaseFeedCapacity());
	int fatDelta = 0;

	if (hasTrait(Trait.FATTY))
	    fatDelta = Math.max(0, food - footDelta);

	return new Species(cardResolver, uid, index, population, size, foodLevel + footDelta, fatLevel + fatDelta, traits,
	        privateTraits);
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

    public boolean hasPrivateTrait(Trait trait) {
	return computeTraits(privateTraits).contains(trait);
    }

    public boolean hasTrait(Trait trait) {
	return computeTraits(traits).contains(trait);
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
	return new Species(cardResolver, uid, index, foodLevel, size, 0, fatLevel, traits, privateTraits);
    }

    private AttackSummary summariseAttack(Player player, TargetAttackContext ac) {
	TargetAttackAnalysis analysis = analyseAttack(player, ac);

	OnAttackPopulationLoss results = null;

	if (analysis.isPossible()) {
	    CostAnalysis evitableDamage = null;
	    PopulationLossSummary attackerDamage = null;
	    Species target = analysis.getTarget();
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
	        .append(", privateTraits=")
	        .append(asTrait(privateTraits))
	        .append("]");
	return builder.toString();
    }

    private List<Trait> asTrait(Map<Integer, CardId> traits) {
	return traits.values().stream().map(c -> cardResolver.resolve(c).getTrait()).collect(Collectors.toList());
    }

    public Species transfertFat() {
	int transfert = Math.min(getBaseFeedCapacity(), fatLevel);

	return new Species(cardResolver, uid, index, population, size, foodLevel + transfert, fatLevel - transfert, traits,
	        privateTraits);
    }
}