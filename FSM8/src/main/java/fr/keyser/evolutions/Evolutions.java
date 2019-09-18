package fr.keyser.evolutions;

import java.util.Collection;
import java.util.function.Predicate;

public final class Evolutions {
    private final Game game;

    private final Player player;

    public Evolutions(Game game) {
	this(game, null);
    }

    private Evolutions(Game game, Player player) {
	this.game = game;
	this.player = player;
    }

    public Evolutions forPlayer(int player) {
	return new Evolutions(null, game.getPlayer(player));
    }

    public Evolutions playFood(CardId foodPlayed) {
	return new Evolutions(null, player.playFood(foodPlayed));
    }

    public Evolutions cleanEndTurn() {
	return new Evolutions(game.cleanEndTurn());
    }

    public Evolutions enterFeedingPhase() {
	return new Evolutions(game
	        .updateFertile()
	        .transfertFat()
	        .feedLongNeck()
	        .fillFoodPool(), null);
    }

    public Game getGame() {
	return game;
    }

    public Player getPlayer() {
	return player;
    }

    public Evolutions feedWateringHole(SpeciesId id) {
	return new Evolutions(game.feedWateringHole(id));
    }

    public Evolutions feedInteligent(SpeciesId id) {
	return new Evolutions(game.feedWateringHole(id));
    }

    public Evolutions mergePlayer(Player player) {
	return new Evolutions(game.mergePlayer(player), null);
    }

    public Evolutions feedCarnivorous(AttackInstructions instruction) {
	return new Evolutions(game.feedCarnivorous(instruction), null);
    }

    public Evolutions feedIntelligent(IntelligentInstructions instruction) {
	return new Evolutions(game.feedIntelligent(instruction), null);
    }

    public Evolutions evolve(EvolutionInstructions instruction) {
	return new Evolutions(null, player.evolve(instruction));
    }

    public Evolutions syncFromGame(Game game) {
	return new Evolutions(null, game.getPlayer(player.getIndex().getId()));
    }

    @Override
    public String toString() {
	if (game != null)
	    return game.toString();
	else if (player != null)
	    return player.toString();
	else
	    return "";
    }

    public Evolutions discard(Collection<CardId> collection) {
	return new Evolutions(game.discard(collection));
    }

    public Evolutions nextTurn() {
	return new Evolutions(game.drawNewTurn(), null);
    }

    public Evolutions nextPlayer() {
	return new Evolutions(game.nextPlayer(), null);
    }

    public Evolutions feedNextPlayer(Predicate<PlayerId> isHungry) {
	return new Evolutions(game.activateCurrentOrNext(isHungry), null);
    }
}