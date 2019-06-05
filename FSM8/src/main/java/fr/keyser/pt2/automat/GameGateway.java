package fr.keyser.pt2.automat;

import static fr.keyser.pt2.automat.PTMultiPlayersAutomatBuilder.draft;
import static fr.keyser.pt2.automat.PTMultiPlayersAutomatBuilder.forPlayer;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.automat.Automat;
import fr.keyser.n.fsm.automat.AutomatContainer;
import fr.keyser.n.fsm.container.AutomatContainerBuilder;
import fr.keyser.n.fsm.container.AutomatContainerBuilder.StateConfigurer;
import fr.keyser.n.fsm.listener.frontier.EntryListener;
import fr.keyser.n.fsm.listener.timeout.TimeOut;
import fr.keyser.pt2.LocalGame;
import fr.keyser.pt2.LocalPlayer;
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

	private void expect(Class<?> expectedInput, InstanceId id) {
	    this.expectedInput = expectedInput;
	    this.id = id;
	}

	public Class<?> getExpectedInput() {
	    return expectedInput;
	}

	public boolean handleInput(Object input) {
	    if (expectedInput != null && expectedInput.isAssignableFrom(input.getClass())) {
		automatContainer.receive(Event.event("done").id(id).put(INPUT, input));
		return true;
	    } else {
		return false;
	    }
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
		if (evt instanceof TimeOut) {
		    consumer.accept(pg, null);
		} else {
		    I input = type.cast(evt.get(INPUT));
		    consumer.accept(pg, input);
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
	draftPhase(acb);
	deployPhase(acb);

	return acb.build();
    }

    private void deployPhase(AutomatContainerBuilder acb) {
	State deploy = PTMultiPlayersAutomatBuilder.deploy();
	for (int index = 0; index < players.size(); ++index) {
	    State forPlayer = forPlayer(deploy, index);

	    inputHandler(PlayInstructionDTO.class, PlayerGateway::doPlay)
	            .configure(acb.state(forPlayer.sub(PTMultiPlayersAutomatBuilder.PLAY)));

	    PlayerGateway playerGateway = players.get(index);

	    acb.state(forPlayer.sub(PTMultiPlayersAutomatBuilder.CHECK_INPUT))
	            .with(PTMultiPlayersAutomatBuilder.HAS_INPUT, playerGateway::hasDeployEffects);

	}
    }

    private void distribute() {
	game.distribute(twoPlayers ? 9 : 5);
    }

    private void draftPhase(AutomatContainerBuilder acb) {
	acb.state(draft(0)).entry(this::distribute);

	Class<? extends PickInstructionDTO> pickClass = twoPlayers ? PickAndDiscardInstructionDTO.class : PickInstructionDTO.class;

	for (int step = 0; step < 4; ++step) {
	    State draftStep = draft(step);
	    acb.state(draftStep).exit(game::passCardsToNext);

	    for (int index = 0; index < players.size(); ++index) {
		inputHandler(pickClass, PlayerGateway::doPick)
		        .configure(acb.state(forPlayer(draftStep, index)));
	    }
	}
    }

    private PlayerGateway asPlayer(InstanceState is) {
	int index = (Integer) is.getProps().get(AutomatContainer.INDEX);
	return players.get(index);
    }

    private <I> StateInputHandler<I> inputHandler(Class<I> type, BiConsumer<PlayerGateway, I> consumer) {
	return new StateInputHandler<>(type, consumer);
    }
}
