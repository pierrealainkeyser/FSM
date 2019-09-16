package fr.keyser.evolutions;

import java.util.List;

import org.springframework.expression.spel.standard.SpelExpressionParser;

import fr.keyser.n.fsm.State;
import fr.keyser.nn.fsm.builder.AutomatsBuilder;
import fr.keyser.nn.fsm.builder.Choice;
import fr.keyser.nn.fsm.builder.Node;
import fr.keyser.nn.fsm.builder.Region;
import fr.keyser.nn.fsm.builder.SpringELChoice;
import fr.keyser.nn.fsm.builder.Terminal;
import fr.keyser.nn.fsm.builder.TransitionNode;
import fr.keyser.nn.fsm.impl.Automats;
import fr.keyser.nn.fsm.impl.ChoicePredicate;
import fr.keyser.nn.fsm.impl.EventMsg;
import fr.keyser.nn.fsm.impl.Instance;
import fr.keyser.nn.fsm.impl.Merge;
import fr.keyser.nn.fsm.impl.TransitionGuard;

public class EvolutionsAutomatsBuilder {

    private static final String ACTIVATE_EVENT = "activate";
    private static final String BOOTSTRAP_NODE = "bootstrap";
    private static final String CARNIVOROUS_EVENT = "carnivorous";
    private static final ChoicePredicate<Evolutions> CHOICE_ALL_IDLE = ChoicePredicate.allChildsMatch(new State("player", "idle"));
    private static final String DONE_EVENT = "done";
    private static final String EOG = "end";
    private static final String EVOLVE_DONE_EVENT = "evolveDone";
    private static final String EVOLVE_EVENT = "evolve";
    private static final String FEED_EVENT = "feed";
    private static final String FILL_EVENT = "fill";
    private static final String FOOD_PLAYED_EVENT = "foodPlayed";
    private static final String GAME_NODE = "game";
    private static final TransitionGuard<Evolutions> GUARD_ALL_IDLE = TransitionGuard.allChildsMatch(new State("player", "idle"));
    private static final String INTELLIGENT_EVENT = "intelligent";
    private static final String JOIN_EVENT = "join";
    private static final String PLAY_CARD_EVENT = "playCard";

    private static final State PLAYER_FEED_IDLE = new State("player", "feed", "idle");
    private static final String PLAYER_NODE = "player";
    private static final String SYNC_EVENT = "sync";
    private static final String WATERING_HOLE_EVENT = "wateringHole";

    private static void broadcastFeed(Instance<Evolutions> i, EventMsg msg) {
	i.broadcast(FEED_EVENT);
    }

    private static void broadcastFill(Instance<Evolutions> i, EventMsg msg) {
	i.broadcast(FILL_EVENT);
    }

    private static void broadcastJoinIgnoreMerge(Instance<Evolutions> i, EventMsg e) {
	if (e instanceof Merge)
	    return;
	i.broadcast(JOIN_EVENT);
    }

    private static Evolutions cleanEndTurn(Evolutions evolutions, EventMsg evt) {
	return evolutions.cleanEndTurn();
    }

    private final static Evolutions createPlayer(Evolutions e, EventMsg event, int index) {
	return e.forPlayer(index);
    }

    private static Evolutions enterFeedingPhase(Evolutions e, EventMsg evt) {
	return e.enterFeedingPhase();
    }

    private static void evolveCurrentPlayer(Instance<Evolutions> i, EventMsg msg) {
	int index = i.get(Evolutions::getGame).getActivePlayer();
	Instance<Evolutions> active = i.childsInstances().get(index);
	active.unicast(EVOLVE_EVENT);
    }

    private static void evolveDone(Instance<Evolutions> i, EventMsg e) {
	i.getParent().unicast(EVOLVE_DONE_EVENT);
    }

    private static void fastMerge(Instance<Evolutions> i, EventMsg evt) {
	i.sendMerge(-1, i.get(Evolutions::getPlayer));
    }

    private static Instance<Evolutions> feedNextPlayer(Instance<Evolutions> i, EventMsg msg) {
	return i.update(e -> {

	    List<Instance<Evolutions>> childs = i.childsInstances();

	    e = e.feedNextPlayer(id -> {
		Instance<Evolutions> iplayer = childs.get(id.getId());
		return PLAYER_FEED_IDLE.equals(iplayer.getState());
	    });

	    int activePlayer = e.getGame().getActivePlayer();
	    childs.get(activePlayer).unicast(ACTIVATE_EVENT);

	    return e;
	});
    }

    private static Evolutions feedWateringHole(Evolutions e, EventMsg evt) {
	return e.feedWateringHole((SpeciesId) evt.getPayload());
    }

    private static void forwardDone(Instance<Evolutions> i, EventMsg evt) {
	i.getParent().unicast(DONE_EVENT);
    }

    private static void forwardEvolutionInstruction(Instance<Evolutions> i, EventMsg evt) {
	i.getParent().unicast(EVOLVE_EVENT, evt.getPayload());
    }

    private static void forwardFoodPlayed(Instance<Evolutions> i, EventMsg evt) {
	i.sendMerge(i.get(Evolutions::getPlayer));
	i.getParent().unicast(FOOD_PLAYED_EVENT);
    }

    private static void forwardWateringHole(Instance<Evolutions> i, EventMsg evt) {
	i.getParent().unicast(WATERING_HOLE_EVENT,
	        i.get(e -> new SpeciesId(e.getPlayer().getIndex(), (Integer) evt.getPayload())));
    }

    private final static Evolutions mergePlayer(Evolutions e, Object o) {
	if (o instanceof Player)
	    return e.mergePlayer((Player) o);
	return e;
    }

    private static Evolutions nextPlayerAfterFeed(Evolutions e, EventMsg evt) {
	String key = evt.getKey();
	if (CARNIVOROUS_EVENT.equals(key) || WATERING_HOLE_EVENT.equals(key) || DONE_EVENT.equals(key)) {
	    return e.nextPlayer();
	}

	return e;
    }

    private static Evolutions nextTurn(Evolutions e, EventMsg msg) {
	return e.nextTurn();
    }

    private static Evolutions playFood(Evolutions e, EventMsg evt) {
	return e.playFood((CardId) evt.getPayload());
    }

    private static Evolutions processEvolutionInstruction(Evolutions e, EventMsg evt) {
	return e.evolve((EvolutionInstructions) evt.getPayload());
    }

    private static Evolutions syncFromGame(Evolutions e, EventMsg evt) {
	Object payload = evt.getPayload();
	if (payload instanceof Game) {
	    return e.syncFromGame((Game) payload);
	}
	return e;
    }

    private static void syncPlayers(Instance<Evolutions> i, EventMsg msg) {
	i.broadcast(SYNC_EVENT, i.get(Evolutions::getGame));
    }

    private static Evolutions updateEvolution(Evolutions e, EventMsg evt) {
	String key = evt.getKey();
	if (EVOLVE_DONE_EVENT.equals(key)) {
	    return e.nextPlayer();
	} else if (EVOLVE_EVENT.equals(key)) {
	    EvolutionInstructions instr = (EvolutionInstructions) evt.getPayload();
	    return e.discard(instr.discarded());
	}
	return e;
    }

    public Automats<Evolutions> build(int nbPlayers) {
	AutomatsBuilder<Evolutions> builder = new AutomatsBuilder<>();

	Region<Evolutions> bootstrap = builder.region(BOOTSTRAP_NODE, nbPlayers)
	        .merge(EvolutionsAutomatsBuilder::mergePlayer)
	        .create(EvolutionsAutomatsBuilder::createPlayer);

	Node<Evolutions> player = builder.node(PLAYER_NODE);
	Node<Evolutions> game = builder.node(GAME_NODE)
	        .allowMerge();

	Terminal<Evolutions> end = builder.terminal(EOG);

	bootstrap.auto(PLAYER_NODE).to(player);
	bootstrap.autoJoinTo(game);

	createGame(game, end);
	createPlayer(player);

	return builder.build();
    }

    private void createGame(Node<Evolutions> game, Terminal<Evolutions> end) {
	Choice<Evolutions> checkTurn = game.choice("checkTurn");
	Node<Evolutions> fillingPool = game.node("filling");
	Node<Evolutions> evolutions = game.node("evolutions");
	Node<Evolutions> feeding = game.node("feeding");
	TransitionNode<Evolutions> endOfTurn = game.auto("endOfTurn");

	SpelExpressionParser parser = new SpelExpressionParser();

	checkTurn.when(new SpringELChoice<>(parser.parseExpression("game.done")), end)
	        .otherwise(fillingPool);

	gameFill(fillingPool, evolutions);
	gameEvolve(evolutions, feeding);
	gameFeeding(feeding, endOfTurn);

	endOfTurn.updateEntry(EvolutionsAutomatsBuilder::cleanEndTurn);
	endOfTurn.to(checkTurn);
    }

    private void createPlayer(Node<Evolutions> player) {
	Node<Evolutions> idle = player.node("idle");
	Node<Evolutions> evolve = player.node("evolve");
	Node<Evolutions> feed = player.node("feed");
	Node<Evolutions> fill = player.node("fill");

	idle.updateExit(EvolutionsAutomatsBuilder::syncFromGame);

	playerFill(player, fill);
	playerEvolve(player, evolve);
	playerFeed(player, feed);

	idle.event(EVOLVE_EVENT, evolve);
	idle.event(FEED_EVENT, feed);
	idle.event(SYNC_EVENT, idle);
	idle.event(FILL_EVENT, fill);
    }

    private void gameEvolve(Node<Evolutions> evolutions, Node<Evolutions> feeding) {

	TransitionNode<Evolutions> activate = evolutions.auto("activate");
	Node<Evolutions> wait = evolutions.node("wait");
	Choice<Evolutions> checkNext = evolutions.choice("checkNext");
	TransitionNode<Evolutions> complete = evolutions.auto("complete");

	activate.to(wait)
	        .callbackEntry(EvolutionsAutomatsBuilder::evolveCurrentPlayer);

	wait.updateExit(EvolutionsAutomatsBuilder::updateEvolution);
	wait.event(EVOLVE_DONE_EVENT, checkNext);
	wait.event(EVOLVE_EVENT, wait);

	SpelExpressionParser parser = new SpelExpressionParser();

	checkNext.when(new SpringELChoice<>(parser.parseExpression("game.activeIsFirst")), complete)
	        .otherwise(activate);

	evolutions.callbackEntry(EvolutionsAutomatsBuilder::syncPlayers);

	complete.callbackEntry(EvolutionsAutomatsBuilder::broadcastJoinIgnoreMerge);
	complete.to(1, feeding);
    }

    private void gameFeeding(Node<Evolutions> feeding, TransitionNode<Evolutions> endOfTurn) {

	feeding.updateEntry(EvolutionsAutomatsBuilder::enterFeedingPhase)
	        .callbackEntry(EvolutionsAutomatsBuilder::syncPlayers)
	        .callbackEntry(EvolutionsAutomatsBuilder::broadcastFeed);

	TransitionNode<Evolutions> delay = feeding.auto("delay");
	Node<Evolutions> wait = feeding.node("wait");
	TransitionNode<Evolutions> carnivorous = feeding.auto("carnivorous");
	TransitionNode<Evolutions> wateringHole = feeding.auto("wateringHole");
	TransitionNode<Evolutions> intelligent = feeding.auto("intelligent");
	Choice<Evolutions> checkDone = feeding.choice("checkDone");

	// delay, so all the players are synced before reaching the waiting step
	delay.to(1, wait);
	wait.entry(EvolutionsAutomatsBuilder::feedNextPlayer);

	wait.event(CARNIVOROUS_EVENT, carnivorous);
	wait.event(WATERING_HOLE_EVENT, wateringHole);
	wait.event(INTELLIGENT_EVENT, intelligent);
	wait.event(DONE_EVENT, checkDone);
	wait.updateExit(EvolutionsAutomatsBuilder::nextPlayerAfterFeed);

	wateringHole.to(checkDone);
	wateringHole.updateEntry(EvolutionsAutomatsBuilder::feedWateringHole)
	        .callbackEntry(EvolutionsAutomatsBuilder::syncPlayers);

	carnivorous.to(checkDone);
	intelligent.to(checkDone);

	checkDone.when(CHOICE_ALL_IDLE, endOfTurn)
	        .otherwise(delay);
    }

    private void gameFill(Node<Evolutions> fillingPool, Node<Evolutions> evolutions) {

	TransitionNode<Evolutions> init = fillingPool.auto("init");
	Node<Evolutions> wait = fillingPool.node("wait");

	init.to(wait)
	        .updateEntry(EvolutionsAutomatsBuilder::nextTurn)
	        .callbackEntry(EvolutionsAutomatsBuilder::syncPlayers)
	        .callbackEntry(EvolutionsAutomatsBuilder::broadcastFill);

	wait.event(FOOD_PLAYED_EVENT, evolutions)
	        .guard(GUARD_ALL_IDLE);

    }

    private void playerEvolve(Node<Evolutions> player, Node<Evolutions> evolve) {
	Node<Evolutions> waiting = evolve.node("waiting");
	TransitionNode<Evolutions> process = evolve.auto("process")
	        .to(waiting);
	TransitionNode<Evolutions> done = evolve.auto("done");
	Node<Evolutions> joining = evolve.node("joining");

	done.callbackEntry(EvolutionsAutomatsBuilder::evolveDone);

	done.to(-1, joining);

	process.updateEntry(EvolutionsAutomatsBuilder::processEvolutionInstruction)
	        .callbackEntry(EvolutionsAutomatsBuilder::forwardEvolutionInstruction);

	waiting.event(EVOLVE_EVENT, process);
	waiting.event(DONE_EVENT, done);

	joining.event(JOIN_EVENT, player);

	joining.callbackExit(EvolutionsAutomatsBuilder::fastMerge);
    }

    private void playerFeed(Node<Evolutions> player, Node<Evolutions> feed) {

	Choice<Evolutions> initial = feed.choice("initial");

	Node<Evolutions> idle = feed.node("idle");
	Choice<Evolutions> mayEat = feed.choice("mayEat", -1);

	Node<Evolutions> waiting = feed.node("waiting");
	TransitionNode<Evolutions> carnivorous = feed.auto("carnivorous");
	TransitionNode<Evolutions> wateringHole = feed.auto("wateringHole");
	TransitionNode<Evolutions> intelligent = feed.auto("intelligent");

	TransitionNode<Evolutions> done = feed.auto("done");
	done.to(-1, player);

	SpelExpressionParser parser = new SpelExpressionParser();

	idle.event(ACTIVATE_EVENT, waiting);
	idle.event(SYNC_EVENT, mayEat);

	idle.updateExit(EvolutionsAutomatsBuilder::syncFromGame);

	SpringELChoice<Evolutions> mayEatChoice = new SpringELChoice<>(parser.parseExpression("player.mayEat(#parent.game)"));
	mayEat.when(mayEatChoice, idle)
	        .otherwise(player);

	// init
	initial.when(mayEatChoice, idle)
	        .otherwise(player);

	carnivorous.to(-1, idle);
	wateringHole.to(-1, idle);
	intelligent.to(-1, idle);

	wateringHole.callbackEntry(EvolutionsAutomatsBuilder::forwardWateringHole);

	waiting.event(CARNIVOROUS_EVENT, carnivorous);
	waiting.event(WATERING_HOLE_EVENT, wateringHole);
	waiting.event(INTELLIGENT_EVENT, intelligent);
	waiting.event(DONE_EVENT, done);

	done.callbackEntry(EvolutionsAutomatsBuilder::forwardDone);

    }

    private void playerFill(Node<Evolutions> player, Node<Evolutions> fill) {

	Node<Evolutions> waitFill = fill.node("wait");
	TransitionNode<Evolutions> doneFill = fill.auto("done")
	        .to(player);
	doneFill.updateEntry(EvolutionsAutomatsBuilder::playFood)
	        .callbackEntry(EvolutionsAutomatsBuilder::forwardFoodPlayed);

	waitFill.event(PLAY_CARD_EVENT, doneFill);
    }

}
