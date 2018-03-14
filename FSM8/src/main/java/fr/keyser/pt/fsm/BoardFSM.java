package fr.keyser.pt.fsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import fr.keyser.fsm.DelayedEventConsumer;
import fr.keyser.fsm.StateMachine;
import fr.keyser.fsm.StateMachineBuilder;
import fr.keyser.fsm.StateMachineBuilder.StateBuilder;
import fr.keyser.pt.Board;

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

    private final Board board;

    private final StateMachine<String, BoardEvent> stateMachine;

    private final List<PlayerBoardFSM> players;

    public BoardFSM(Board board) {
	this.board = board;
	this.players = Collections.unmodifiableList(board.getPlayers().map(p -> new PlayerBoardFSM(p, this)).collect(Collectors.toList()));
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

	turn.onEntry(board::resetCounters);
	draft.onEntry(this.board::distributeCards);

	play.onEntry(this::waitForDeploy);

	deploy.onEntry(this.board::deployPhaseEffect).onEntry(this::waitForInput);
	deploy.onExit(this.board::endOfDeployPhase);

	war.onEntry(this.board::warPhase).onEntry(this::waitFor);
	gold.onEntry(this.board::goldPhase).onEntry(this::waitFor);
	building.onEntry(this.board::buildPhase).onEntry(this::waitForBuilding);

	age.onEntry(this.board::agePhase).onEntry(this::waitForInput);
	age.onExit(this.board::endAgePhase);

	checkEOG.onEntry(() -> {
	    if (board.isLastTurn())
		ec.push(BoardEvent.END);
	    else
		ec.push(BoardEvent.NEXT);
	});
	checkEOG.transition(BoardEvent.NEXT, turn).onTransition(this.board::newTurn);

	this.stateMachine = builder.build();
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
	int count = 5;
	for (int i = 0; i < count; ++i)
	    buildingSub.add(from.sub(i + ""));

	for (int i = 0; i < count; ++i) {
	    StateBuilder<String, BoardEvent> next = to;
	    boolean notLast = i < count - 1;
	    boolean notFirst = i > 0;
	    if (notLast)
		next = buildingSub.get(i + 1);

	    StateBuilder<String, BoardEvent> current = buildingSub.get(i);
	    chainedSubByPlayers(current, next);

	    if (notFirst && notLast)
		current.onEntry(this.board::passCardsToNext);
	}
    }

    private void chainedSubByPlayers(StateBuilder<String, BoardEvent> from, StateBuilder<String, BoardEvent> to) {
	int count = players.size();
	List<StateBuilder<String, BoardEvent>> buildingSub = new ArrayList<>();
	for (int i = 0; i < count; ++i)
	    buildingSub.add(from.sub(i + ""));

	for (int i = 0; i < count; ++i) {
	    StateBuilder<String, BoardEvent> next = to;
	    if (i < count - 1)
		next = buildingSub.get(i + 1);

	    buildingSub.get(i).transition(BoardEvent.NEXT, next);
	}
    }

    public void next() {
	stateMachine.push(BoardEvent.NEXT);
    }
}
