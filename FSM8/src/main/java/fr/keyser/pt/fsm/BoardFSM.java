package fr.keyser.pt.fsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import fr.keyser.fsm.DelayedEventConsumer;
import fr.keyser.fsm.SimpleAction;
import fr.keyser.fsm.StateMachine;
import fr.keyser.fsm.StateMachineBuilder;
import fr.keyser.fsm.StateMachineBuilder.StateBuilder;
import fr.keyser.pt.BoardContract;
import fr.keyser.pt.event.PhaseEvent;
import fr.keyser.pt.event.TurnEvent;

public class BoardFSM {

    public static final String AGE = "age";
    public static final String BUILDING = "building";
    public static final String GOLD = "gold";
    public static final String WAR = "war";
    public static final String DEPLOY = "deploy";
    public static final String DRAFT = "draft";
    private final static String TURN = "TURN";
    private final static String CHECK_EOG = "CHECK_EOG";
    private final static String DRAFT_PHASE = "DRAFT_PHASE";
    private final static String DEPLOY_PHASE = "DEPLOY_PHASE";
    private final static String WAR_PHASE = "WAR_PHASE";
    private final static String GOLD_PHASE = "GOLD_PHASE";
    private final static String BUILDING_PHASE = "BUILDING_PHASE";
    private final static String AGE_PHASE = "AGE_PHASE";

    private enum BoardEvent {
	NEXT, END
    }

    private final BoardContract contract;

    private final StateMachine<String, BoardEvent> stateMachine;

    private final List<PlayerBoardFSM> players;

    private String phase;

    public BoardFSM(BoardContract b) {
	this.contract = b;
	this.players = Collections
	        .unmodifiableList(this.contract.getPlayers().map(p -> new PlayerBoardFSM(p, this)).collect(Collectors.toList()));
	StateMachineBuilder<String, BoardEvent> builder = new StateMachineBuilder<>();
	DelayedEventConsumer<String, BoardEvent> ec = builder.eventConsummer();

	StateBuilder<String, BoardEvent> turn = builder.state(TURN);
	StateBuilder<String, BoardEvent> draft = turn.sub(DRAFT_PHASE);
	StateBuilder<String, BoardEvent> deploy = turn.sub(DEPLOY_PHASE);
	StateBuilder<String, BoardEvent> war = turn.sub(WAR_PHASE);
	StateBuilder<String, BoardEvent> gold = turn.sub(GOLD_PHASE);
	StateBuilder<String, BoardEvent> building = turn.sub(BUILDING_PHASE);
	StateBuilder<String, BoardEvent> age = turn.sub(AGE_PHASE);

	StateBuilder<String, BoardEvent> checkEOG = builder.state(CHECK_EOG);

	draft.onEntry(phase(DRAFT));
	deploy.onEntry(phase(DEPLOY));
	war.onEntry(phase(WAR));
	gold.onEntry(phase(GOLD));
	building.onEntry(phase(BUILDING));
	age.onEntry(phase(AGE));

	chainDraft(draft, deploy);
	chainedSubByPlayers(deploy, war);
	chainedSubByPlayers(war, gold);
	chainedSubByPlayers(gold, building);
	chainedSubByPlayers(building, age);
	chainedSubByPlayers(age, checkEOG);

	turn.onEntry(this.contract::resetCounters);

	draft.onEntry(this::distributeAndWaitCard);
	deploy.onEntry(this::acquireLastDraft);
	deploy.onEntry(this::nextPhase);
	
	war.onEntry(this::nextPhase)
	        .onEntry(this.contract::warPhase);
	gold.onEntry(this::nextPhase);
	building.onEntry(this::nextPhase);
	age.onEntry(this::nextPhase);

	checkEOG.onEntry(() -> {
	    if (this.contract.isLastTurn())
		ec.push(BoardEvent.END);
	    else
		ec.push(BoardEvent.NEXT);
	});
	checkEOG.transition(BoardEvent.NEXT, turn)
	        .onTransition(this::newTurn);

	this.stateMachine = builder.build();
    }

    private void newTurn() {
	this.contract.newTurn();
	fireTurnEvent();
    }

    private SimpleAction phase(String phase) {
	return () -> {
	    this.phase = phase;
	    firePhaseEvent();
	};
    }

    /**
     * JUnit only
     * 
     * @return the current phase
     */
    public String getPhase() {
	return phase;
    }

    public void start() {
	this.stateMachine.enterInitialState();
    }

    private void distributeAndWaitCard() {
	contract.distributeCards();
	nextPhase();
    }

    private void nextPhase() {
	players.forEach(PlayerBoardFSM::nextPhase);
    }

    private void chainDraft(StateBuilder<String, BoardEvent> from, StateBuilder<String, BoardEvent> to) {
	List<StateBuilder<String, BoardEvent>> buildingSub = new ArrayList<>();
	int count = 4;
	for (int i = 0; i < count; ++i)
	    buildingSub.add(from.sub("draft" + i));

	for (int i = 0; i < count; ++i) {
	    StateBuilder<String, BoardEvent> next = to;
	    boolean notLast = i < count - 1;
	    StateBuilder<String, BoardEvent> current = buildingSub.get(i);
	    current.onExit(this::passCardsToNext);
	    if (notLast) {
		next = buildingSub.get(i + 1);
		current.onExit(() -> players.forEach(PlayerBoardFSM::loop));
	    }

	    chainedSubByPlayers(current, next);

	    if (!notLast)
		current.onExit(this::nextPhase);
	}
    }

    private void passCardsToNext() {
	this.contract.passCardsToNext();
    }

    private void acquireLastDraft() {
	this.contract.acquireLastDraft();
    }

    private void chainedSubByPlayers(StateBuilder<String, BoardEvent> from, StateBuilder<String, BoardEvent> to) {
	int count = players.size();
	List<StateBuilder<String, BoardEvent>> buildingSub = new ArrayList<>();
	for (int i = 0; i < count; ++i)
	    buildingSub.add(from.sub("" + (count - i)));

	for (int i = 0; i < count; ++i) {
	    StateBuilder<String, BoardEvent> next = to;
	    if (i < count - 1)
		next = buildingSub.get(i + 1);

	    buildingSub.get(i).transition(BoardEvent.NEXT, next);
	}
    }

    void next() {
	stateMachine.push(BoardEvent.NEXT);
    }

    public List<PlayerBoardFSM> getPlayers() {
	return Collections.unmodifiableList(players);
    }

    void forward(Object event) {
	contract.forward(event);
    }

    public void refresh() {

	firePhaseEvent();
	fireTurnEvent();

	players.forEach(PlayerBoardFSM::fireEvents);

    }

    private void firePhaseEvent() {
	forward(new PhaseEvent(phase));
    }

    private void fireTurnEvent() {
	forward(new TurnEvent(contract.getTurn()));
    }
}
