package fr.keyser.pt.fsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import fr.keyser.fsm.DelayedEventConsumer;
import fr.keyser.fsm.StateMachine;
import fr.keyser.fsm.StateMachineBuilder;
import fr.keyser.fsm.StateMachineBuilder.StateBuilder;
import fr.keyser.pt.BoardContract;

public class BoardFSM {

    private final static String DRAFT = "DRAFT";
    private final static String TURN = "TURN";
    private final static String CHECK_EOG = "CHECK_EOG";
    private final static String PLAY = "PLAY";
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

    public BoardFSM(BoardContract b) {
	this.contract = b;
	this.players = Collections
	        .unmodifiableList(this.contract.getPlayers().map(p -> new PlayerBoardFSM(p, this)).collect(Collectors.toList()));
	StateMachineBuilder<String, BoardEvent> builder = new StateMachineBuilder<>();
	DelayedEventConsumer<String, BoardEvent> ec = builder.eventConsummer();

	StateBuilder<String, BoardEvent> turn = builder.state(TURN);

	StateBuilder<String, BoardEvent> checkEOG = builder.state(CHECK_EOG);

	StateBuilder<String, BoardEvent> draft = turn.sub(DRAFT);
	StateBuilder<String, BoardEvent> play = turn.sub(PLAY);
	StateBuilder<String, BoardEvent> deploy = turn.sub(DEPLOY_PHASE);
	StateBuilder<String, BoardEvent> war = turn.sub(WAR_PHASE);
	StateBuilder<String, BoardEvent> gold = turn.sub(GOLD_PHASE);
	StateBuilder<String, BoardEvent> building = turn.sub(BUILDING_PHASE);
	StateBuilder<String, BoardEvent> age = turn.sub(AGE_PHASE);

	chainDraft(draft, play);

	chainedSubByPlayers(play, deploy);
	chainedSubByPlayers(deploy, war);
	chainedSubByPlayers(war, gold);
	chainedSubByPlayers(gold, building);
	chainedSubByPlayers(building, age);
	chainedSubByPlayers(age, checkEOG);

	turn.onEntry(this.contract::resetCounters);
	draft.onEntry(this::distributeAndWaitCard);

	play.onEntry(this::waitForDeploy);

	deploy.onEntry(this.contract::deployPhaseEffect)
	        .onEntry(this::waitForInput)
	        .onExit(this.contract::endOfDeployPhase);

	war.onEntry(this.contract::warPhase).onEntry(this::waitFor);
	gold.onEntry(this.contract::goldPhase).onEntry(this::waitFor);
	building.onEntry(this.contract::buildPhase)
	        .onEntry(this::waitForBuilding)
	        .onExit(this.contract::endBuildPhase);

	age.onEntry(this.contract::agePhase)
	        .onEntry(this::waitForInput)
	        .onExit(this.contract::endAgePhase);

	checkEOG.onEntry(() -> {
	    if (this.contract.isLastTurn())
		ec.push(BoardEvent.END);
	    else
		ec.push(BoardEvent.NEXT);
	});
	checkEOG.transition(BoardEvent.NEXT, turn)
	        .onTransition(this.contract::newTurn);

	this.stateMachine = builder.build();
    }

    public void start() {
	this.stateMachine.enterInitialState();
    }

    private void distributeAndWaitCard() {
	contract.distributeCards();
	players.forEach(PlayerBoardFSM::waitForDraft);
    }

    private void waitForInput() {
	players.forEach(PlayerBoardFSM::waitForInput);
    }

    private void waitForBuilding() {
	players.forEach(PlayerBoardFSM::waitForBuilding);
    }

    private void waitForDeploy() {
	players.forEach(PlayerBoardFSM::waitForDeploy);
    }

    private void waitFor() {
	players.forEach(PlayerBoardFSM::waitFor);
    }

    private void chainDraft(StateBuilder<String, BoardEvent> from, StateBuilder<String, BoardEvent> to) {
	List<StateBuilder<String, BoardEvent>> buildingSub = new ArrayList<>();
	int count = 4;
	for (int i = 0; i < count; ++i)
	    buildingSub.add(from.sub("d" + i));

	for (int i = 0; i < count; ++i) {
	    StateBuilder<String, BoardEvent> next = to;
	    boolean notLast = i < count - 1;
	    if (notLast)
		next = buildingSub.get(i + 1);

	    StateBuilder<String, BoardEvent> current = buildingSub.get(i);
	    chainedSubByPlayers(current, next);

	    current.onExit(this.contract::passCardsToNext);
	}
    }

    private void chainedSubByPlayers(StateBuilder<String, BoardEvent> from, StateBuilder<String, BoardEvent> to) {
	int count = players.size();
	List<StateBuilder<String, BoardEvent>> buildingSub = new ArrayList<>();
	for (int i = 0; i < count; ++i)
	    buildingSub.add(from.sub("" + i));

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
}
