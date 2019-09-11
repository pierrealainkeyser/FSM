package fr.keyser.evolutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {

    private static List<Player> createPlayers(int count, CardResolver resolver) {
	List<Player> p = new ArrayList<>();
	for (int i = 0; i < count; ++i)
	    p.add(new Player(resolver, i));
	return p;
    }

    private final int activePlayer;

    private final List<CardId> decks;

    private final List<CardId> discard;

    private final boolean done;

    private final int firstPlayer;

    private final int foodPool;

    private final List<Player> players;

    public Game(int players, CardResolver resolver, int first, List<CardId> decks) {
	this(first, createPlayers(players, resolver), first, 0, decks, Collections.emptyList(), false);
    }

    private Game(int activePlayer, List<Player> players, int firstPlayer, int foodPool, List<CardId> decks, List<CardId> discard,
            boolean done) {
	this.activePlayer = activePlayer;
	this.players = players;
	this.firstPlayer = firstPlayer;
	this.foodPool = foodPool;
	this.decks = Collections.unmodifiableList(decks);
	this.discard = Collections.unmodifiableList(discard);
	this.done = done;
    }

    public Game activateCurrentOrNext(Predicate<PlayerId> isHungry) {

	int index = activePlayer;

	do {
	    PlayerId id = players.get(index).getIndex();
	    if (isHungry.test(id)) {
		return new Game(index, players, firstPlayer, foodPool, decks, discard, done);
	    }
	    index = (index + 1) % players.size();
	} while (index != activePlayer);

	return this;
    }

    public Game feedCarnivorous(SpeciesId attackerID, SpeciesId victimId, boolean preventAttack) {
	Game current = this;

	Species attacker = current.getSpecies(attackerID);
	Player player = current.getPlayer(attackerID);
	Species victim = current.getSpecies(victimId);
	CarnivorousContext ctx = current.carnivorousContext(victim);
	AttackSummary summary = attacker.attacksSummaries(player, ctx).get(0);

	OnAttackPopulationLoss populationLoss = summary.getPopulationLoss();

	FeedingActionContext feeding = ctx.createContext();
	PopulationLossSummary victimLoss = populationLoss.getVictim();
	feeding.get(victim)
	        .reduceCapacity(victimLoss);

	PopulationLossSummary attackerLoss = populationLoss.getAttacker();
	SpeciesFeedingContext attackerCtx = feeding.get(attacker);
	if (attackerLoss != null && !preventAttack) {
	    attackerCtx
	            .reduceCapacity(attackerLoss);
	}
	attackerCtx.feedAttack(victim);

	current = current.players(feedAll(feeding));

	current = current.consumeLoss(victimId, victimLoss);

	if (attackerLoss != null && !preventAttack) {
	    current = current.consumeLoss(attackerID, attackerLoss);
	}

	return current;
    }

    public boolean isActiveIsFirst() {
	return firstPlayer == activePlayer;
    }

    private CarnivorousContext carnivorousContext(Species target) {

	Player player = getPlayer(target);
	return new CarnivorousContext(scavengersCtx(), Arrays.asList(player.attackForSpecies(target)));
    }

    private Game consumeLoss(SpeciesId speciesId, PopulationLossSummary loss) {
	Game current = this;
	Player player = current.getPlayer(speciesId)
	        .populationLoss(speciesId, loss);
	if (loss.isExtinct() && loss.getDrawTraits() > 0) {
	    List<CardId> draws = new ArrayList<>();
	    current = current.draw(loss.getDrawTraits(), draws::addAll);
	    player = player.draws(draws);

	}
	return current.mergePlayer(player);
    }

    public Game deltaFoodPool(List<Player> players, int delta) {
	return new Game(activePlayer, players, firstPlayer, Math.max(0, foodPool + delta), decks, discard, done);
    }

    private Game draw(int ammount, Consumer<List<CardId>> callback) {

	List<CardId> drawn = new ArrayList<>();

	List<CardId> remaining = new ArrayList<>();
	List<CardId> newDiscard = this.discard;

	boolean done = this.done;
	if (decks.size() >= ammount) {
	    drawn.addAll(decks.subList(0, ammount));
	    remaining.addAll(decks.subList(ammount, decks.size()));
	} else {
	    drawn.addAll(decks);
	    List<CardId> newDeck = new ArrayList<>(discard);
	    Collections.shuffle(newDeck);

	    int drawMore = ammount - drawn.size();

	    drawn.addAll(newDeck.subList(0, drawMore));
	    remaining.addAll(newDeck.subList(drawMore, newDeck.size()));

	    newDiscard = new ArrayList<>();
	    done = true;
	}

	callback.accept(drawn);

	return new Game(activePlayer, players, firstPlayer, foodPool, remaining, newDiscard, done);
    }

    public Game nextPlayer() {
	return new Game((activePlayer + 1) % players.size(), players, firstPlayer, foodPool, decks, discard, done);
    }

    private Game nextFirstPlayer() {
	int nfirst = (firstPlayer + 1) % players.size();
	return new Game(nfirst, players, nfirst, foodPool, decks, discard, done);
    }

    public Game discard(Collection<CardId> collection) {
	List<CardId> discard = new ArrayList<>(this.discard);
	discard.addAll(collection);
	return new Game(activePlayer, players, firstPlayer, foodPool, decks, discard, done);

    }

    public Game drawNewTurn() {
	Game current = this;
	List<Player> players = new ArrayList<>();
	for (Player p : this.players) {
	    List<CardId> draws = new ArrayList<>();
	    current = current.draw(3 + p.getSpeciesCount(), draws::addAll);
	    players.add(p.draws(draws));
	}
	return current.players(players);
    }

    private List<Player> feedAll(FeedingActionContext ctx) {
	List<Player> players = new ArrayList<>(this.players);
	for (FeedingSummary f : ctx.summary()) {
	    int index = f.getSpecies().getPlayer().getId();
	    Player p = players.get(index);

	    players.set(index, p.feed(f));
	}
	return players;
    }

    public Game transfertFat() {
	return operationPlayers(Player::transfertFat);
    }

    private Game operationPlayers(UnaryOperator<Player> mapper) {
	return players(players.stream().map(mapper)
	        .collect(Collectors.toList()));
    }

    public Game cleanEndTurn() {

	Game current = operationPlayers(Player::doScore);
	List<Player> newPlayers = new ArrayList<>();
	for (Player p : current.players) {
	    int toDraw = p.getExtinctionCardsToDraw();
	    if (toDraw > 0) {
		List<CardId> draws = new ArrayList<>(toDraw);
		current = current.draw(toDraw, draws::addAll);
		newPlayers.add(p.clearExtinctsSpecies(draws));
	    } else
		newPlayers.add(p);
	}
	return current.players(newPlayers).nextFirstPlayer();
    }

    public Game updateFertile() {
	if (foodPool <= 0)
	    return this;

	return operationPlayers(Player::updateFertile);
    }

    public Game feedLongNeck() {

	FeedingActionContext longNeck = new FeedingActionContext(this);

	this.players.stream()
	        .flatMap(Player::longNecks)
	        .forEach(s -> longNeck.get(s).feedLongNeck());

	return players(feedAll(longNeck));
    }

    public Game feedOmnivorous(SpeciesId id) {

	FeedingActionContext ctx = new FeedingActionContext(this);
	WateringHole hole = new WateringHole(foodPool);

	ctx.get(getSpecies(id))
	        .feedWateringHole(hole);

	return deltaFoodPool(feedAll(ctx), -hole.getConsumed());
    }

    public Game fillFoodPool() {
	Integer delta = this.players.stream().flatMap(p -> p.getFoodPlayed())
	        .collect(Collectors.reducing(Integer.valueOf(0), (l, r) -> l + r));

	List<Player> players = this.players.stream()
	        .map(Player::clearFoodPlayed)
	        .collect(Collectors.toList());

	return deltaFoodPool(players, delta);
    }

    public int getFoodPool() {
	return foodPool;
    }

    public Player getPlayer(int index) {
	return players.get(index);
    }

    public Player getPlayer(Species species) {
	return getPlayer(species.getUid());
    }

    public Player getPlayer(SpeciesId species) {
	return players.get(species.getPlayer().getId());
    }

    private Species getSpecies(SpeciesId id) {
	return players.get(id.getPlayer().getId()).getSpecies(id);
    }

    public boolean hasPreyFor(Player owner, Species carnivorous) {
	return targets()
	        .filter(t -> t.isSelf(carnivorous))
	        .anyMatch(t -> {
	            TargetAttackAnalysis attack = carnivorous.analyseAttack(owner, t);
	            return attack.isPossible();
	        });

    }

    public boolean isDone() {
	return done;
    }

    public Game mergePlayer(Player player) {
	List<Player> players = new ArrayList<>(this.players);
	players.set(player.getIndex().getId(), player);
	return players(players);
    }

    private Game players(List<Player> players) {
	return new Game(activePlayer, players, firstPlayer, foodPool, decks, discard, done);
    }

    private FeedingActionContext scavengersCtx() {
	FeedingActionContext scavengers = new FeedingActionContext(this);

	players.stream()
	        .flatMap(Player::scavengers)
	        .forEach(s -> scavengers.get(s).feedScavenger());
	return scavengers;
    }

    private Stream<TargetAttackContext> targets() {
	return players.stream()
	        .flatMap(Player::targetAttackContexts);
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Game [activePlayer=").append(activePlayer).append(", decks=").append(decks).append(", discard=").append(discard)
	        .append(", done=").append(done).append(", firstPlayer=").append(firstPlayer).append(", foodPool=").append(foodPool)
	        .append(", players=").append(players).append("]");
	return builder.toString();
    }

    public int getActivePlayer() {
	return activePlayer;
    }
}