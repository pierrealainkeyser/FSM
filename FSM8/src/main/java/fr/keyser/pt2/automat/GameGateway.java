package fr.keyser.pt2.automat;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.Event.Builder;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.automat.Automat;
import fr.keyser.n.fsm.automat.AutomatContainer;
import fr.keyser.n.fsm.automat.AutomatContainerBuilder;
import fr.keyser.n.fsm.automat.AutomatContainerBuilder.StateConfigurer;
import fr.keyser.n.fsm.automat.TimeOut;
import fr.keyser.n.fsm.listener.frontier.EntryListener;
import fr.keyser.pt2.LocalGame;
import fr.keyser.pt2.LocalPlayer;
import fr.keyser.pt2.view.ActivateEffectDTO;
import fr.keyser.pt2.view.BuildInstructionDTO;
import fr.keyser.pt2.view.PickAndDiscardInstructionDTO;
import fr.keyser.pt2.view.PickInstructionDTO;
import fr.keyser.pt2.view.PlayInstructionDTO;

public class GameGateway {

    public class PlayerGateway {
	private Class<?> expectedInput;

	private InstanceId id;

	private final LocalPlayer player;

	private boolean waiting = false;

	public PlayerGateway(LocalPlayer player) {
	    this.player = player;
	}

	private void doPick(PickInstructionDTO pick) {
	    if (pick == null) {
		// timeout
	    } else {
		player.pick(pick.getPick());
	    }
	}

	private void doPlay(PlayInstructionDTO play) {

	}

	private void doActive(ActivateEffectDTO activate) {

	}

	private void doBuild(BuildInstructionDTO build) {

	}

	private void expect(Class<?> expectedInput, InstanceId id) {
	    this.expectedInput = expectedInput;
	    this.id = id;
	}

	public Class<?> getExpectedInput() {
	    return expectedInput;
	}

	public boolean handleInput(Object input) {
	    Builder event = Event.event("done").id(id);
	    if (expectedInput == null) {
		automatContainer.receive(event);
	    } else if (expectedInput.isAssignableFrom(input.getClass())) {
		automatContainer.receive(event.put(INPUT, input));
		return true;
	    }
	    return false;

	}

	private boolean hasAgeEffects() {
	    return !player.ageEffects().isEmpty();
	}

	private boolean hasDeployEffects() {
	    return !player.deployEffects().isEmpty();
	}

	public boolean isWaiting() {
	    return waiting;
	}

	private void setWaiting(boolean waiting) {
	    this.waiting = waiting;
	}
    }

    private class StateInputHandler<I> {
	private final BiConsumer<PlayerGateway, I> consumer;

	private final Class<I> type;

	public StateInputHandler(Class<I> type, BiConsumer<PlayerGateway, I> consumer) {
	    this.type = type;
	    this.consumer = consumer;
	}

	private StateConfigurer configure(StateConfigurer configurer) {
	    return configurer.entry(entry()).exit(exit());
	}

	private EntryListener entry() {
	    return (is, state, event) -> {
		PlayerGateway pg = asPlayer(is);
		pg.setWaiting(true);
		pg.expect(type, is.getId());
	    };
	}

	private EntryListener exit() {
	    return (is, state, evt) -> {
		PlayerGateway pg = asPlayer(is);
		pg.setWaiting(false);
		Object obj = evt.get(INPUT);
		if (evt instanceof TimeOut || obj == null) {
		    consumer.accept(pg, null);
		} else {
		    consumer.accept(pg, type.cast(obj));
		}
	    };
	}
    }

    private static final String INPUT = "input";

    private final AutomatContainer automatContainer;

    private final LocalGame game;

    private final List<PlayerGateway> players;

    private final boolean twoPlayers;

    public GameGateway(Automat automat, LocalGame game) {
	this.game = game;
	this.players = Collections.unmodifiableList(game.getPlayers().stream().map(PlayerGateway::new).collect(Collectors.toList()));
	this.automatContainer = build(automat);
	this.twoPlayers = players.size() == 2;
    }

    private AutomatContainer build(Automat automat) {
	AutomatContainerBuilder acb = new AutomatContainerBuilder(automat);

	acb.state(new State(GameConst.INIT_TURN))
	        .entry(game::nextTurn);

	draftPhase(acb);
	deployPhase(acb);
	warPhase(acb);
	goldPhase(acb);
	buildPhase(acb);
	agePhase(acb);

	acb.state(new State(GameConst.END_OF_TURN))
	        .with(GameConst.NEXT_TURN_CHOICE, game::hasNextTurn);

	return acb.build();
    }

    private void deployPhase(AutomatContainerBuilder acb) {
	State deploy = new State(GameConst.TURN, GameConst.DEPLOY);

	acb.state(new State(GameConst.TURN, GameConst.INIT_DEPLOY)).entry(game::deployPhase);
	acb.state(deploy).exit(game::endDeployPhase);

	for (int index = 0; index < players.size(); ++index) {
	    State forPlayer = GameConst.forPlayer(deploy, index);

	    inputHandler(PlayInstructionDTO.class, PlayerGateway::doPlay)
	            .configure(acb.state(forPlayer.sub(GameConst.PLAY)));

	    PlayerGateway playerGateway = players.get(index);

	    acb.state(forPlayer.sub(GameConst.CHECK_INPUT))
	            .with(GameConst.HAS_INPUT, playerGateway::hasDeployEffects);

	    inputHandler(ActivateEffectDTO.class, PlayerGateway::doActive)
	            .configure(acb.state(forPlayer.sub(GameConst.WAITING_INPUT)));

	}
    }

    private void agePhase(AutomatContainerBuilder acb) {
	State age = new State(GameConst.TURN, GameConst.AGE);
	acb.state(new State(GameConst.TURN, GameConst.INIT_AGE)).entry(game::agePhase);
	acb.state(age).exit(game::endAgePhase);

	for (int index = 0; index < players.size(); ++index) {
	    State forPlayer = GameConst.forPlayer(age, index);

	    PlayerGateway playerGateway = players.get(index);

	    acb.state(forPlayer.sub(GameConst.CHECK_INPUT))
	            .with(GameConst.HAS_INPUT, playerGateway::hasAgeEffects);

	    acb.state(forPlayer.sub(GameConst.HAS_MORE_INPUT))
	            .with(GameConst.HAS_INPUT, playerGateway::hasAgeEffects);

	    inputHandler(ActivateEffectDTO.class, PlayerGateway::doActive)
	            .configure(acb.state(forPlayer.sub(GameConst.WAITING_INPUT)));

	    noOpHandler().configure(acb.state(forPlayer.sub(GameConst.WAITING)));
	}
    }

    private void warPhase(AutomatContainerBuilder acb) {
	State war = new State(GameConst.TURN, GameConst.WAR);
	acb.state(new State(GameConst.TURN, GameConst.INIT_WAR))
	        .entry(game::warPhase);

	noopPhase(acb, war);
    }

    private void goldPhase(AutomatContainerBuilder acb) {
	State gold = new State(GameConst.TURN, GameConst.GOLD);
	acb.state(new State(GameConst.TURN, GameConst.INIT_GOLD))
	        .entry(game::payPhase);
	noopPhase(acb, gold);
    }

    private void buildPhase(AutomatContainerBuilder acb) {
	State build = new State(GameConst.TURN, GameConst.BUILD);
	for (int index = 0; index < players.size(); ++index) {
	    State forPlayer = GameConst.forPlayer(build, index);

	    inputHandler(BuildInstructionDTO.class, PlayerGateway::doBuild)
	            .configure(acb.state(forPlayer));

	}
    }

    private void noopPhase(AutomatContainerBuilder acb, State base) {
	for (int index = 0; index < players.size(); ++index) {
	    State forPlayer = GameConst.forPlayer(base, index);
	    noOpHandler().configure(acb.state(forPlayer));
	}
    }

    private void distribute() {
	game.distribute(twoPlayers ? 9 : 5);
    }

    private void draftPhase(AutomatContainerBuilder acb) {
	State draft = new State(GameConst.TURN, GameConst.DRAFT);
	acb.state(draft.sub(GameConst.INIT)).entry(this::distribute);
	acb.state(draft).exit(game::pickLastCard);

	Class<? extends PickInstructionDTO> pickClass = twoPlayers ? PickAndDiscardInstructionDTO.class : PickInstructionDTO.class;

	for (int step = 0; step < 4; ++step) {
	    State draftStep = draft.sub(GameConst.draftStep(step));
	    acb.state(draftStep).exit(game::passCardsToNext);

	    for (int index = 0; index < players.size(); ++index) {
		inputHandler(pickClass, PlayerGateway::doPick)
		        .configure(acb.state(GameConst.forPlayer(draftStep, index)));
	    }
	}
    }

    private PlayerGateway asPlayer(InstanceState is) {
	int index = (Integer) is.getProps().get(AutomatContainer.INDEX);
	return players.get(index);
    }

    private <I> StateInputHandler<I> noOpHandler() {
	return new StateInputHandler<>(null, (pg, i) -> {
	});
    }

    private <I> StateInputHandler<I> inputHandler(Class<I> type, BiConsumer<PlayerGateway, I> consumer) {
	return new StateInputHandler<>(type, consumer);
    }

    public void start() {
	automatContainer.start();
    }

    public List<PlayerGateway> getPlayers() {
	return players;
    }
}
