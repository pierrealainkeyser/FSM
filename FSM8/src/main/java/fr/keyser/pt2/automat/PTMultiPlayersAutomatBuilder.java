package fr.keyser.pt2.automat;

import static fr.keyser.n.fsm.automat.ChoiceTransitionSourceBuilder.choice;
import static fr.keyser.n.fsm.automat.StateBuilder.auto;
import static fr.keyser.n.fsm.automat.StateBuilder.join;

import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.automat.Automat;
import fr.keyser.n.fsm.automat.AutomatBuilder;
import fr.keyser.n.fsm.automat.StateBuilder;

public class PTMultiPlayersAutomatBuilder {

    public static final String PLAY = "Play";
    private static final String END_OF_TURN = "endOfTurn";
    private static final String AGE = "age";
    private static final String BUILD = "build";
    private static final String GOLD = "gold";
    private static final String WAR = "war";
    private static final String DEPLOY = "deploy";
    private static final String DRAFT = "draft";
    private static final String PREPARE_NEW_TURN = "prepareNewTurn";
    private static final String TURN = "turn";
    public static final String NEXT_TURN_CHOICE = "nextTurn";
    private static final String DONE_STATE = "Done";
    private static final String DONE = "done";
    public static final String WAITING = "Waiting";
    public static final String HAS_MORE_INPUT = "HasMoreInput";
    public static final String WAITING_INPUT = "WaitingInput";
    public static final String CHECK_INPUT = "CheckInput";
    public static final String HAS_INPUT = "hasInput";
    private final int nbPlayers;

    public PTMultiPlayersAutomatBuilder(int nbPlayers) {
	this.nbPlayers = nbPlayers;
    }

    public static State prepareNewTurn() {
	return new State(PREPARE_NEW_TURN);
    }

    public static State draft() {
	return new State(TURN, DRAFT);
    }

    public static State draft(int step) {
	return draft().sub(draftStep(step));
    }

    public static State deploy() {
	return new State(TURN, DEPLOY);
    }

    public static State buildPhase() {
	return new State(TURN, BUILD);
    }

    public static State war() {
	return new State(TURN, WAR);
    }

    public static State gold() {
	return new State(TURN, GOLD);
    }

    public static State age() {
	return new State(TURN, AGE);
    }

    public static State endOfTurn() {
	return new State(END_OF_TURN);
    }

    public static State forPlayer(State state, int player) {
	return state.sub(forPlayer(player));
    }

    private static String draftStep(int step) {
	return "D" + step;
    }

    private void createWaiting(StateBuilder parent, StateBuilder dest) {
	StateBuilder done = parent.joining(DONE_STATE);
	for (int i = 0; i < nbPlayers; ++i) {
	    StateBuilder p = parent.state(forPlayer(i));
	    p.onEvent(DONE, done);
	    p.on(StateBuilder.timeout(), done);
	}
	parent.on(join(), dest);
    }

    private static String forPlayer(int player) {
	return "P" + player;
    }

    private void createWaitingDeploy(StateBuilder parent, StateBuilder dest) {
	StateBuilder done = parent.joining(DONE_STATE);
	for (int i = 0; i < nbPlayers; ++i) {
	    StateBuilder base = parent.state(forPlayer(i));
	    StateBuilder play = base.state(PLAY);
	    StateBuilder check = base.choice(CHECK_INPUT);
	    StateBuilder waitingInput = base.state(WAITING_INPUT);

	    play.onEvent(DONE, check);
	    play.on(StateBuilder.timeout(), check);

	    check.on(choice(HAS_INPUT, waitingInput)
	            .otherwise(done));

	    waitingInput.onEvent(DONE, check);
	    waitingInput.on(StateBuilder.timeout(), done);

	}
	parent.on(join(), dest);
    }

    private void createWaitingAge(StateBuilder parent, StateBuilder dest) {
	StateBuilder done = parent.joining(DONE_STATE);
	for (int i = 0; i < nbPlayers; ++i) {
	    StateBuilder base = parent.state(forPlayer(i));
	    StateBuilder check = base.choice(CHECK_INPUT);
	    StateBuilder waitingInput = base.state(WAITING_INPUT);
	    StateBuilder hasMoreInput = base.choice(HAS_MORE_INPUT);
	    StateBuilder waiting = base.state(WAITING);

	    check.on(choice(HAS_INPUT, waitingInput)
	            .otherwise(waiting));

	    hasMoreInput.on(choice(HAS_INPUT, waitingInput)
	            .otherwise(done));

	    waitingInput.onEvent(DONE, hasMoreInput);
	    waitingInput.on(StateBuilder.timeout(), done);

	    waiting.onEvent(DONE, done);
	    waiting.on(StateBuilder.timeout(), done);
	}
	parent.on(join(), dest);
    }

    public Automat build() {
	AutomatBuilder builder = new AutomatBuilder();

	StateBuilder prepare = builder.auto(PREPARE_NEW_TURN);

	StateBuilder turn = builder.state(TURN);

	StateBuilder draft = turn.state(DRAFT);
	StateBuilder deploy = turn.orthogonal(DEPLOY);
	StateBuilder war = turn.orthogonal(WAR);
	StateBuilder gold = turn.orthogonal(GOLD);
	StateBuilder build = turn.orthogonal(BUILD);
	StateBuilder age = turn.orthogonal(AGE);
	StateBuilder endOfTurn = builder.choice(END_OF_TURN);
	StateBuilder end = builder.terminal("ended");

	prepare.on(auto(), turn);

	StateBuilder cur = draft.orthogonal(draftStep(0));
	for (int i = 1; i <= 4; ++i) {
	    StateBuilder next = deploy;
	    if (i < 4)
		next = draft.orthogonal(draftStep(i));

	    createWaiting(cur, next);
	    cur = next;
	}

	createWaitingDeploy(deploy, war);
	createWaiting(war, gold);
	createWaiting(gold, build);
	createWaiting(build, age);
	createWaitingAge(age, endOfTurn);

	endOfTurn.on(choice(NEXT_TURN_CHOICE, prepare).otherwise(end));

	return builder.build();
    }

}
