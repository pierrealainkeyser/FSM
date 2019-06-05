package fr.keyser.pt2.automat;

import static fr.keyser.n.fsm.automat.ChoiceTransitionSourceBuilder.choice;
import static fr.keyser.n.fsm.automat.StateBuilder.join;

import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.automat.Automat;
import fr.keyser.n.fsm.automat.AutomatBuilder;
import fr.keyser.n.fsm.automat.StateBuilder;

public class PTMultiPlayersAutomatBuilder {

    private static final String PLAY = "Play";
    private static final String END_OF_TURN_CHOICE = "endOfTurn";
    private static final String AGE = "age";
    private static final String BUILD = "build";
    private static final String GOLD = "gold";
    private static final String WAR = "war";
    private static final String DEPLOY = "deploy";
    private static final String DRAFT = "draft";
    private static final String TURN = "turn";
    private static final String NEXT_TURN_CHOICE = "nextTurn";
    private static final String DONE_STATE = "Done";
    private static final String DONE = "done";
    private static final String WAITING = "Waiting";
    private static final String HAS_MORE_INPUT = "HasMoreInput";
    private static final String WAITING_INPUT = "WaitingInput";
    private static final String CHECK_INPUT = "CheckInput";
    private static final String HAS_INPUT = "hasInput";
    private final int nbPlayers;

    public PTMultiPlayersAutomatBuilder(int nbPlayers) {
	this.nbPlayers = nbPlayers;
    }

    public static State draft(int step) {
	return new State(TURN, DRAFT, draftStep(step));
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

	StateBuilder turn = builder.state(TURN);

	StateBuilder draft = turn.state(DRAFT);
	StateBuilder deploy = turn.orthogonal(DEPLOY);
	StateBuilder war = turn.orthogonal(WAR);
	StateBuilder gold = turn.orthogonal(GOLD);
	StateBuilder build = turn.orthogonal(BUILD);
	StateBuilder age = turn.orthogonal(AGE);
	StateBuilder endOfTurn = builder.choice(END_OF_TURN_CHOICE);
	StateBuilder end = builder.terminal("ended");

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

	endOfTurn.on(choice(NEXT_TURN_CHOICE, turn).otherwise(end));

	return builder.build();
    }

}
