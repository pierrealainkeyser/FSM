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

public class GameGateway {

    private static final String INPUT = "input";

    public class PlayerGateway {
	private final LocalPlayer player;

	private boolean waiting = false;

	private Class<?> expectedInput;

	private InstanceId id;

	public PlayerGateway(LocalPlayer player) {
	    this.player = player;
	}

	void expect(Class<?> expectedInput, InstanceId id) {
	    this.expectedInput = expectedInput;
	    this.id = id;
	    this.waiting = true;
	}

	public void handleInput(Object input) {
	    automatContainer.receive(Event.event("done").id(id).put(INPUT, input));
	}

	public Class<?> getExpectedInput() {
	    return expectedInput;
	}

	void doPick(PickInstructionDTO pick) {
	    waiting = false;
	    if (pick == null) {
		// timeout
	    } else {
		player.pick(pick.getPick());
	    }
	}

	public boolean isWaiting() {
	    return waiting;
	}
    }

    private final LocalGame game;

    private final List<PlayerGateway> players;

    private final AutomatContainer automatContainer;

    private final boolean twoPlayers;

    public GameGateway(Automat automat, LocalGame game) {
	this.game = game;
	this.players = Collections.unmodifiableList(game.getPlayers().stream().map(PlayerGateway::new).collect(Collectors.toList()));
	this.automatContainer = build(automat);
	this.twoPlayers = players.size() == 2;
    }

    private EntryListener wrap(BiConsumer<InstanceState, PlayerGateway> toWrap) {
	return (is, state, event) -> toWrap.accept(is, getPlayer(is));

    }

    private PlayerGateway getPlayer(InstanceState is) {
	int index = (Integer) is.getProps().get(AutomatContainer.INDEX);
	return players.get(index);
    }

    private EntryListener event(BiConsumer<Event, PlayerGateway> toWrap) {
	return (is, state, event) -> toWrap.accept(event, getPlayer(is));
    }

    private AutomatContainer build(Automat automat) {
	AutomatContainerBuilder acb = new AutomatContainerBuilder(automat);
	draftPhase(acb);

	return acb.build();
    }

    private void draftPhase(AutomatContainerBuilder acb) {
	acb.state(draft(0)).entry(game::distribute);

	Class<?> pickClass = twoPlayers ? PickAndDiscardInstructionDTO.class : PickInstructionDTO.class;

	for (int step = 0; step < 4; ++step) {
	    State draftStep = draft(step);
	    acb.state(draftStep).exit(game::passCardsToNext);

	    for (int index = 0; index < players.size(); ++index) {
		StateConfigurer playerDraftStepConfigurer = acb.state(forPlayer(draftStep, index));
		playerDraftStepConfigurer.entry(expect(pickClass));
		playerDraftStepConfigurer.exit(event(this::processPick));
	    }
	}
    }

    private EntryListener expect(Class<?> expect) {
	return wrap((is, pg) -> pg.expect(expect, is.getId()));
    }

    private void processPick(Event evt, PlayerGateway pg) {
	if (evt instanceof TimeOut) {
	    pg.doPick(null);
	} else {
	    PickInstructionDTO pick = (PickInstructionDTO) evt.get(INPUT);
	    pg.doPick(pick);
	}
    }

}
