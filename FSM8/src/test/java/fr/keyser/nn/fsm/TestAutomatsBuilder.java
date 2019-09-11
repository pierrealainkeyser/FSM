package fr.keyser.nn.fsm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import fr.keyser.evolutions.CardId;
import fr.keyser.evolutions.EvolutionInstructions;
import fr.keyser.evolutions.Evolutions;
import fr.keyser.evolutions.Game;
import fr.keyser.evolutions.MapCardResolver;
import fr.keyser.evolutions.Player;
import fr.keyser.evolutions.PlayerId;
import fr.keyser.evolutions.PlayerView;
import fr.keyser.evolutions.PlayerViewBuilder;
import fr.keyser.evolutions.SpeciesId;
import fr.keyser.evolutions.Trait;
import fr.keyser.n.fsm.InstanceId;
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
import fr.keyser.nn.fsm.impl.PayloadActionFunction;
import fr.keyser.nn.fsm.impl.Start;
import fr.keyser.nn.fsm.impl.TransitionGuard;

class TestAutomatsBuilder {

    protected void createEvolve(Node<Evolutions> player, Node<Evolutions> evolve) {
	Node<Evolutions> waiting = evolve.node("waiting");
	TransitionNode<Evolutions> process = evolve.auto("process")
	        .to(waiting);
	TransitionNode<Evolutions> done = evolve.auto("done");
	Node<Evolutions> joining = evolve.node("joining");

	done.callbackEntry((i, e) -> i.getParent().unicast("evolveDone"));

	done.to(-1, joining);

	process.updateEntry((e, evt) -> e.evolve((EvolutionInstructions) evt.getPayload()));

	waiting.event("evolve", process);
	waiting.event("done", done);

	joining.event("join", player);

	joining.callbackExit((i, evt) -> i.sendMerge(-1, i.get(Evolutions::getPlayer)));

    }

    protected void createFeed(Node<Evolutions> player, Node<Evolutions> feed) {

	Choice<Evolutions> initial = feed.choice("initial");

	Node<Evolutions> idle = feed.node("idle");
	Choice<Evolutions> mayEat = feed.choice("mayEat", -1);

	Node<Evolutions> waiting = feed.node("waiting");
	TransitionNode<Evolutions> carnivorous = feed.auto("carnivorous");
	TransitionNode<Evolutions> omnivorous = feed.auto("omnivorous");
	TransitionNode<Evolutions> intelligent = feed.auto("intelligent");

	TransitionNode<Evolutions> done = feed.auto("done");
	done.to(-1, player);

	SpelExpressionParser parser = new SpelExpressionParser();

	PayloadActionFunction<Evolutions> syncFromGame = (e, evt) -> {
	    Object payload = evt.getPayload();
	    if (payload instanceof Game) {
		return e.syncFromGame((Game) payload);
	    }
	    return e;
	};

	idle.event("activate", waiting);
	idle.event("sync", mayEat);

	idle.updateExit(syncFromGame);

	SpringELChoice<Evolutions> mayEatChoice = new SpringELChoice<>(parser.parseExpression("player.mayEat(#parent.game)"));
	mayEat.when(mayEatChoice, idle)
	        .otherwise(player);

	// init
	initial.when(mayEatChoice, idle)
	        .otherwise(player);

	carnivorous.to(-1, idle);
	omnivorous.to(-1, idle);
	intelligent.to(-1, idle);

	omnivorous.callbackEntry((i, evt) -> i.getParent().unicast("omnivorous",
	        new SpeciesId(i.get(e -> e.getPlayer().getIndex()), (Integer) evt.getPayload())));

	waiting.event("carnivorous", carnivorous);
	waiting.event("omnivorous", omnivorous);
	waiting.event("intelligent", intelligent);
	waiting.event("done", done);

	done.callbackEntry((i, e) -> i.getParent().unicast("done"));

    }

    @Test
    void testEvolutions() throws JsonProcessingException {
	int nbPlayers = 3;

	SpelExpressionParser parser = new SpelExpressionParser();

	AutomatsBuilder<Evolutions> builder = new AutomatsBuilder<>();
	Region<Evolutions> region = builder.region("bootstrap", nbPlayers)
	        .merge((e, o) -> {
	            if (o instanceof Player)
		        return e.mergePlayer((Player) o);
	            return e;
	        })
	        .create((e, m, i) -> e.forPlayer(i));

	Node<Evolutions> player = builder.node("player");
	Node<Evolutions> game = builder.node("game")
	        .allowMerge();

	Terminal<Evolutions> end = builder.terminal("end");

	region.auto("player").to(player);
	region.autoJoinTo(game);

	Node<Evolutions> idle = player.node("idle");
	Node<Evolutions> evolve = player.node("evolve");
	Node<Evolutions> feed = player.node("feed");
	Node<Evolutions> fill = player.node("fill");

	Node<Evolutions> waitFill = fill.node("wait");
	TransitionNode<Evolutions> doneFill = fill.auto("done")
	        .to(idle);
	doneFill.updateEntry((e, evt) -> e.playFood((CardId) evt.getPayload()))
	        .callbackEntry((i, evt) -> {
	            i.sendMerge(i.get(Evolutions::getPlayer));
	            i.getParent().unicast("foodPlayed");
	        });

	waitFill.event("playCard", doneFill);

	PayloadActionFunction<Evolutions> syncFromGame = (e, evt) -> {
	    Object payload = evt.getPayload();
	    if (payload instanceof Game) {
		return e.syncFromGame((Game) payload);
	    }
	    return e;
	};
	idle.updateExit(syncFromGame);

	createEvolve(player, evolve);
	createFeed(player, feed);

	idle.event("evolve", evolve);
	idle.event("feed", feed);
	idle.event("sync", idle);
	idle.event("fill", fill);

	Choice<Evolutions> checkTurn = game.choice("checkTurn");
	Node<Evolutions> fillingPool = game.node("filling");
	TransitionNode<Evolutions> initFood = fillingPool.auto("init");
	Node<Evolutions> waitFood = fillingPool.node("wait");

	Node<Evolutions> evolutions = game.node("evolutions");
	TransitionNode<Evolutions> activate = evolutions.auto("activate");
	Node<Evolutions> wait = evolutions.node("wait");
	Choice<Evolutions> checkNext = evolutions.choice("checkNext");
	TransitionNode<Evolutions> complete = evolutions.auto("complete");

	complete.callbackEntry((i, e) -> {
	    if (e instanceof Merge)
		return;
	    i.broadcast("join");

	});

	Node<Evolutions> feeding = game.node("feeding");
	complete.to(1, feeding);

	Node<Evolutions> endOfTurn = game.node("endOfTurn");

	activate.to(wait)
	        .callbackEntry((i, e) -> {
	            int index = i.get(Evolutions::getGame).getActivePlayer();
	            Instance<Evolutions> active = i.childsInstances().get(index);
	            active.unicast("evolve");
	        });
	wait.updateExit((e, evt) -> {
	    if ("evolveDone".equals(evt.getKey())) {
		return e.nextPlayer();
	    }
	    return e;
	});
	wait.event("evolveDone", checkNext);

	checkNext.when(new SpringELChoice<>(parser.parseExpression("game.activeIsFirst")), complete)
	        .otherwise(activate);

	checkTurn.when(new SpringELChoice<>(parser.parseExpression("game.done")), end)
	        .otherwise(fillingPool);

	BiConsumer<Instance<Evolutions>, EventMsg> syncPlayers = (i, evt) -> i.broadcast("sync", i.get(Evolutions::getGame));
	initFood.to(waitFood)
	        .updateEntry((e, evt) -> e.nextTurn())
	        .callbackEntry(syncPlayers)
	        .callbackEntry((i, evt) -> i.broadcast("fill"));

	waitFood.event("foodPlayed", evolutions)
	        .guard(TransitionGuard.allChildsMatch(idle.state()));
	evolutions.callbackEntry(syncPlayers);

	feeding.updateEntry((e, evt) -> e.enterFeedingPhase())
	        .callbackEntry(syncPlayers)
	        .callbackEntry((i, evt) -> i.broadcast("feed"));

	TransitionNode<Evolutions> delay = feeding.auto("delay");
	Node<Evolutions> feedWaiting = feeding.node("waiting");
	delay.to(1, feedWaiting);

	State playerFeedIdle = new State("player", "feed", "idle");

	feedWaiting.entry((i, evt) -> {
	    return i.update(e -> {

		List<Instance<Evolutions>> childs = i.childsInstances();

		e = e.feedNextPlayer(id -> {
		    Instance<Evolutions> iplayer = childs.get(id.getId());
		    return playerFeedIdle.equals(iplayer.getState());
		});

		int activePlayer = e.getGame().getActivePlayer();
		childs.get(activePlayer).unicast("activate");

		return e;
	    });
	});

	TransitionNode<Evolutions> carnivorous = feeding.auto("carnivorous");
	TransitionNode<Evolutions> omnivorous = feeding.auto("omnivorous");
	TransitionNode<Evolutions> intelligent = feeding.auto("intelligent");

	Choice<Evolutions> checkDone = feeding.choice("checkDone");
	carnivorous.to(checkDone);
	omnivorous.to(checkDone);
	intelligent.to(checkDone);

	omnivorous.updateEntry((e, evt) -> e.feedOmnivorous((SpeciesId) evt.getPayload()))
	        .callbackEntry(syncPlayers);

	feedWaiting.event("carnivorous", carnivorous);
	feedWaiting.event("omnivorous", omnivorous);
	feedWaiting.event("intelligent", intelligent);

	checkDone.when(ChoicePredicate.allChildsMatch(idle.state()), endOfTurn)
	        .otherwise(delay);

	feedWaiting.event("done", checkDone);

	feedWaiting
	        .updateExit((e, evt) -> {
	            String key = evt.getKey();
	            if ("carnivorous".equals(key) || "omnivorous".equals(key) || "done".equals(key)) {
		        return e.nextPlayer();
	            }

	            return e;
	        });

	Automats<Evolutions> automats = builder.build();
	System.out.println(automats);

	MapCardResolver resolver = new MapCardResolver();
	CardId alarmId = resolver.card(Trait.ALARM, 3);
	resolver.card(Trait.FORAGING, 6);
	CardId longNeckId = resolver.card(Trait.LONG_NECK, 4);
	resolver.card(Trait.FERTILE, 5);
	CardId carnivorousId = resolver.card(Trait.CARNIVOROROUS, 3);
	resolver.card(Trait.PACK_HUNTER, 2);
	resolver.card(Trait.INTELIGGENT, 4);
	CardId climberId = resolver.card(Trait.CLIMBER, 5);
	CardId collaborativeId = resolver.card(Trait.COLLABORATIVE, -2);
	resolver.card(Trait.DEFENSIVE_HORDE, 6);
	resolver.card(Trait.LONG_NECK, -2);
	resolver.card(Trait.EMBUSCADE, 2);

	List<CardId> decks = new ArrayList<>(resolver.ids());

	automats.submit(Start.start(new Evolutions(new Game(nbPlayers, resolver, 0, decks))));

	// watering hole
	InstanceId player1 = new InstanceId("1");
	InstanceId player2 = new InstanceId("2");
	InstanceId player3 = new InstanceId("3");
	automats.submit(EventMsg.unicast("playCard", player1, alarmId));
	automats.submit(EventMsg.unicast("playCard", player2, carnivorousId));
	automats.submit(EventMsg.unicast("playCard", player3, collaborativeId));

	EvolutionInstructions evolveP1 = new EvolutionInstructions();
	evolveP1.setIndex(0);
	evolveP1.getTraits().put(0, longNeckId);

	// evolve
	automats.submit(EventMsg.unicast("evolve", player1, evolveP1));
	automats.submit(EventMsg.unicast("done", player1));

	dump(automats);



	EvolutionInstructions evolveP2 = new EvolutionInstructions();
	evolveP2.setIndex(-1);
	evolveP2.getNewSpecies().add(climberId);

	automats.submit(EventMsg.unicast("evolve", player2, evolveP2));
	automats.submit(EventMsg.unicast("done", player2));
	
	dumpView(automats, new PlayerId(1));

	dump(automats);

	automats.submit(EventMsg.unicast("done", player3));

	dump(automats);

	// feed
	automats.submit(EventMsg.unicast("omnivorous", player2, 0));

	dump(automats);
	automats.submit(EventMsg.unicast("omnivorous", player3, 0));
	dump(automats);

	automats.submit(EventMsg.unicast("omnivorous", player2, 1));
	dump(automats);
    }

    private void dump(Automats<Evolutions> automats) {
	System.out.println("-------------");
	System.out.println(automats.instances().stream().map(s -> s.toString()).collect(Collectors.joining("\n")));
	System.out.println("-------------");
    }

    private void dumpView(Automats<Evolutions> automats, PlayerId forPlayer) throws JsonProcessingException {
	Game g = automats.instances().get(0).get(Evolutions::getGame);
	List<Player> players = automats.instances().stream().skip(1).map(e -> e.get(Evolutions::getPlayer)).collect(Collectors.toList());

	PlayerViewBuilder builder = new PlayerViewBuilder(g, players);
	ObjectMapper om = new ObjectMapper();
	ObjectWriter ow = om.writer().withDefaultPrettyPrinter();

	PlayerView view = builder.getViews().get(forPlayer);
	System.out.println(ow.writeValueAsString(view));
    }

    @Test
    void testGame() {
	MapCardResolver resolver = new MapCardResolver();
	resolver.card(Trait.ALARM, 3);
	resolver.card(Trait.FORAGING, 6);
	resolver.card(Trait.FATTY, 4);
	resolver.card(Trait.FERTILE, 5);
	resolver.card(Trait.CARNIVOROROUS, 3);
	resolver.card(Trait.CARNIVOROROUS, 2);
	resolver.card(Trait.CARNIVOROROUS, 4);
	resolver.card(Trait.CARNIVOROROUS, 5);
	resolver.card(Trait.CARNIVOROROUS, 6);
	resolver.card(Trait.CARNIVOROROUS, -2);
	resolver.card(Trait.EMBUSCADE, 2);
	resolver.card(Trait.COLLABORATIVE, -2);

	List<CardId> decks = new ArrayList<>(resolver.ids());

	Game g = new Game(3, resolver, 0, decks);
	Game stared = g.drawNewTurn();

	Player p0 = stared.getPlayer(0);

	p0 = p0.playFood(p0.getInHands().get(0));

	EvolutionInstructions ei = new EvolutionInstructions();
	ei.setIndex(0);
	ei.getTraits().put(0, p0.getInHands().get(0));

	p0 = p0.evolve(ei);

	EvolutionInstructions ei2 = new EvolutionInstructions();
	ei2.setIndex(-1);
	ei2.getNewSpecies().add(p0.getInHands().get(0));
	ei2.getTraits().put(0, p0.getInHands().get(1));

	p0 = p0.evolve(ei2);

	System.out.println(stared.mergePlayer(p0));
    }

}
