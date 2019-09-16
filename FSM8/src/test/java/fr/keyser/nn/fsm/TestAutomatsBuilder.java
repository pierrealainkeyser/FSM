package fr.keyser.nn.fsm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import fr.keyser.evolutions.CardId;
import fr.keyser.evolutions.EvolutionInstructions;
import fr.keyser.evolutions.Evolutions;
import fr.keyser.evolutions.EvolutionsAutomatsBuilder;
import fr.keyser.evolutions.FeedingOperation;
import fr.keyser.evolutions.Game;
import fr.keyser.evolutions.GamePhase;
import fr.keyser.evolutions.MapCardResolver;
import fr.keyser.evolutions.Player;
import fr.keyser.evolutions.PlayerAndStatus;
import fr.keyser.evolutions.PlayerId;
import fr.keyser.evolutions.PlayerStatus;
import fr.keyser.evolutions.PlayerView;
import fr.keyser.evolutions.PlayerViewBuilder;
import fr.keyser.evolutions.SpeciesId;
import fr.keyser.evolutions.Trait;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.State;
import fr.keyser.nn.fsm.impl.Automats;
import fr.keyser.nn.fsm.impl.EventMsg;
import fr.keyser.nn.fsm.impl.Instance;
import fr.keyser.nn.fsm.impl.Start;

class TestAutomatsBuilder {

    private static GamePhase asPhase(State gameState) {
	if (new State("game", "filling", "wait").equals(gameState))
	    return GamePhase.FILL;

	if (new State("game", "evolutions", "wait").equals(gameState))
	    return GamePhase.EVOLVE;

	if (new State("game", "feeding", "wait").equals(gameState))
	    return GamePhase.FEED;

	return GamePhase.ENDED;

    }

    private static PlayerStatus asStatus(GamePhase phase, State state) {

	State idle = new State("player", "idle");

	if (GamePhase.FILL == phase) {
	    if (idle.equals(state))
		return PlayerStatus.DONE;
	    else
		return PlayerStatus.ACTIVE;

	} else if (GamePhase.EVOLVE == phase) {

	    if (new State("player", "evolve", "joining").equals(state))
		return PlayerStatus.DONE;
	    else if (new State("player", "evolve", "waiting").equals(state))
		return PlayerStatus.ACTIVE;
	    else
		return PlayerStatus.WAITING;

	} else if (GamePhase.FEED == phase) {
	    if (idle.equals(state))
		return PlayerStatus.DONE;
	    else if (new State("player", "feed", "waiting").equals(state))
		return PlayerStatus.ACTIVE;
	    else
		return PlayerStatus.WAITING;
	}

	return null;
    }

    private EvolutionInstructions createWithTraits(int index, CardId carnivorousId, CardId... size) {
	EvolutionInstructions ei = new EvolutionInstructions();
	ei.setIndex(0);
	ei.getSize().addAll(Arrays.asList(size));
	ei.getTraits().put(index, carnivorousId);
	return ei;
    }

    private ObjectWriter createWriter() {
	SimpleModule sm = new SimpleModule();
	sm.addSerializer(PlayerId.class, new StdScalarSerializer<PlayerId>(PlayerId.class) {

	    @Override
	    public void serialize(PlayerId value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString("" + value.getId());

	    }
	});

	sm.addSerializer(CardId.class, new StdScalarSerializer<CardId>(CardId.class) {

	    @Override
	    public void serialize(CardId value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString("" + value.getId());

	    }
	});

	sm.addSerializer(SpeciesId.class, new StdScalarSerializer<SpeciesId>(SpeciesId.class) {

	    @Override
	    public void serialize(SpeciesId value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(value.getPlayer().getId() + "#" + value.getId());

	    }
	});

	ObjectWriter ow = new ObjectMapper().registerModule(sm).writer().withDefaultPrettyPrinter();
	return ow;
    }

    private void dump(Automats<Evolutions> automats) {
	System.out.println("-------------");
	System.out.println(automats.instances().stream().map(s -> s.toString()).collect(Collectors.joining("\n")));
	System.out.println("-------------");
    }

    private void dumpView(Automats<Evolutions> automats, PlayerId forPlayer) throws JsonProcessingException {
	Instance<Evolutions> instance = automats.instances().get(0);
	Game g = instance.get(Evolutions::getGame);
	GamePhase gamePhase = asPhase(instance.getState());

	List<PlayerAndStatus> players = automats.instances().stream().skip(1)
	        .map(e -> new PlayerAndStatus(e.get(Evolutions::getPlayer), asStatus(gamePhase, e.getState())))
	        .collect(Collectors.toList());

	PlayerViewBuilder builder = new PlayerViewBuilder(gamePhase, g, players);

	ObjectWriter ow = createWriter();

	PlayerView view = builder.getViews().get(forPlayer);
	System.out.println(ow.writeValueAsString(view));
    }

    @Test
    void testCarnivorous() throws JsonProcessingException {
	MapCardResolver resolver = new MapCardResolver();
	CardId carnivorousId = resolver.card(Trait.CARNIVOROROUS, 3);
	CardId moreId = resolver.card(Trait.CARNIVOROROUS, 3);
	CardId scavengerId = resolver.card(Trait.SCAVENGER, 3);
	CardId hornedId = resolver.card(Trait.HORNED, 3);
	CardId cimberId = resolver.card(Trait.CLIMBER, 3);
	CardId intelligentId = resolver.card(Trait.INTELIGGENT, 3);
	CardId intelligent2Id = resolver.card(Trait.INTELIGGENT, 3);

	Game g = new Game(4, resolver, 0, new ArrayList<>(resolver.ids()));

	Player carnivorous = g.getPlayer(0)
	        .draws(Arrays.asList(carnivorousId, moreId, intelligentId, intelligent2Id))
	        .evolve(createWithTraits(0, carnivorousId, moreId))
	        .evolve(createWithTraits(1, intelligentId));
	g = g.mergePlayer(carnivorous);

	Player horned = g.getPlayer(1)
	        .draws(Arrays.asList(hornedId))
	        .evolve(createWithTraits(0, hornedId));
	g = g.mergePlayer(horned);

	Player scavenger = g.getPlayer(2)
	        .draws(Arrays.asList(scavengerId))
	        .evolve(createWithTraits(0, scavengerId));
	g = g.mergePlayer(scavenger);

	Player climber = g.getPlayer(3)
	        .draws(Arrays.asList(cimberId))
	        .evolve(createWithTraits(0, cimberId));
	g = g.mergePlayer(climber);

	List<FeedingOperation> opts = carnivorous.computeFeedingOperations(g);
	System.out.println(createWriter().writeValueAsString(opts));

    }

    @Test
    void testEvolutionsBuilder() throws JsonProcessingException {
	int nbPlayers = 3;

	Automats<Evolutions> automats = new EvolutionsAutomatsBuilder().build(nbPlayers);
	System.out.println(automats);

	MapCardResolver resolver = new MapCardResolver();
	CardId alarmId = resolver.card(Trait.ALARM, 3);
	resolver.card(Trait.FORAGING, 6);
	CardId longNeckId = resolver.card(Trait.LONG_NECK, 4);
	CardId fertileId = resolver.card(Trait.FERTILE, 5);
	CardId carnivorousId = resolver.card(Trait.CARNIVOROROUS, 3);
	resolver.card(Trait.PACK_HUNTER, 2);
	CardId collaborativeId = resolver.card(Trait.COLLABORATIVE, 4);
	CardId climberId = resolver.card(Trait.CLIMBER, 5);
	CardId intelligentId = resolver.card(Trait.INTELIGGENT, -2);
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

	dump(automats);
	dumpView(automats, new PlayerId(1));

	automats.submit(EventMsg.unicast("playCard", player3, intelligentId));

	EvolutionInstructions evolveP1 = new EvolutionInstructions();
	evolveP1.setIndex(0);
	evolveP1.getTraits().put(0, longNeckId);
	evolveP1.getSize().add(fertileId);

	// evolve
	automats.submit(EventMsg.unicast("evolve", player1, evolveP1));
	automats.submit(EventMsg.unicast("done", player1));

	dump(automats);

	EvolutionInstructions evolveP2 = new EvolutionInstructions();
	evolveP2.setIndex(-1);
	evolveP2.getNewSpecies().add(climberId);
	evolveP2.getTraits().put(0, collaborativeId);

	automats.submit(EventMsg.unicast("evolve", player2, evolveP2));
	automats.submit(EventMsg.unicast("done", player2));

	dumpView(automats, new PlayerId(1));

	dump(automats);

	automats.submit(EventMsg.unicast("done", player3));

	dump(automats);

	// feed
	dumpView(automats, new PlayerId(1));

	automats.submit(EventMsg.unicast("wateringHole", player2, 0));

	dump(automats);
	automats.submit(EventMsg.unicast("wateringHole", player3, 0));
	dump(automats);
	dumpView(automats, new PlayerId(1));

	automats.submit(EventMsg.unicast("wateringHole", player2, 1));
	dump(automats);
	dumpView(automats, new PlayerId(0));
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
