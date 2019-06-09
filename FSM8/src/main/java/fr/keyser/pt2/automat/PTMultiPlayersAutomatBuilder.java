package fr.keyser.pt2.automat;

import static fr.keyser.n.fsm.automat.ChoiceTransitionSourceBuilder.choice;
import static fr.keyser.n.fsm.automat.StateBuilder.auto;
import static fr.keyser.n.fsm.automat.StateBuilder.join;

import fr.keyser.n.fsm.automat.Automat;
import fr.keyser.n.fsm.automat.AutomatBuilder;
import fr.keyser.n.fsm.automat.StateBuilder;

public class PTMultiPlayersAutomatBuilder {

    private final int nbPlayers;

    public PTMultiPlayersAutomatBuilder(int nbPlayers) {
	this.nbPlayers = nbPlayers;
    }

    private void createWaiting(StateBuilder parent, StateBuilder dest) {
	StateBuilder done = parent.joining(GameConst.DONE_STATE);
	for (int i = 0; i < nbPlayers; ++i) {
	    StateBuilder p = parent.state(GameConst.forPlayer(i));
	    p.onEvent(GameConst.DONE, done);
	    p.on(StateBuilder.timeout(), done);
	}
	parent.on(join(), dest);
    }

    private void createWaitingDeploy(StateBuilder parent, StateBuilder dest) {
	StateBuilder done = parent.joining(GameConst.DONE_STATE);
	for (int i = 0; i < nbPlayers; ++i) {
	    StateBuilder base = parent.state(GameConst.forPlayer(i));
	    StateBuilder play = base.state(GameConst.PLAY);
	    StateBuilder check = base.choice(GameConst.CHECK_INPUT);
	    StateBuilder waitingInput = base.state(GameConst.WAITING_INPUT);

	    play.onEvent(GameConst.DONE, check);
	    play.on(StateBuilder.timeout(), check);

	    check.on(choice(GameConst.HAS_INPUT, waitingInput)
	            .otherwise(done));

	    waitingInput.onEvent(GameConst.DONE, check);
	    waitingInput.on(StateBuilder.timeout(), done);

	}
	parent.on(join(), dest);
    }

    private void createWaitingAge(StateBuilder parent, StateBuilder dest) {
	StateBuilder done = parent.joining(GameConst.DONE_STATE);
	for (int i = 0; i < nbPlayers; ++i) {
	    StateBuilder base = parent.state(GameConst.forPlayer(i));
	    StateBuilder check = base.choice(GameConst.CHECK_INPUT);
	    StateBuilder waitingInput = base.state(GameConst.WAITING_INPUT);
	    StateBuilder hasMoreInput = base.choice(GameConst.HAS_MORE_INPUT);
	    StateBuilder waiting = base.state(GameConst.WAITING);

	    check.on(choice(GameConst.HAS_INPUT, waitingInput)
	            .otherwise(waiting));

	    hasMoreInput.on(choice(GameConst.HAS_INPUT, waitingInput)
	            .otherwise(done));

	    waitingInput.onEvent(GameConst.DONE, hasMoreInput);
	    waitingInput.on(StateBuilder.timeout(), done);

	    waiting.onEvent(GameConst.DONE, done);
	    waiting.on(StateBuilder.timeout(), done);
	}
	parent.on(join(), dest);
    }

    public Automat build() {
	AutomatBuilder builder = new AutomatBuilder();

	StateBuilder prepare = builder.auto(GameConst.INIT_TURN);

	StateBuilder turn = builder.state(GameConst.TURN);

	StateBuilder draft = turn.state(GameConst.DRAFT);

	StateBuilder deployInit = turn.auto(GameConst.INIT_DEPLOY);
	StateBuilder deploy = turn.orthogonal(GameConst.DEPLOY);

	StateBuilder warInit = turn.auto(GameConst.INIT_WAR);
	StateBuilder war = turn.orthogonal(GameConst.WAR);

	StateBuilder goldInit = turn.auto(GameConst.INIT_GOLD);
	StateBuilder gold = turn.orthogonal(GameConst.GOLD);

	StateBuilder build = turn.orthogonal(GameConst.BUILD);

	StateBuilder ageInit = turn.auto(GameConst.INIT_AGE);
	StateBuilder age = turn.orthogonal(GameConst.AGE);
	StateBuilder endOfTurn = builder.choice(GameConst.END_OF_TURN);
	StateBuilder end = builder.terminal("ended");

	prepare.on(auto(), turn);

	deployInit.on(auto(), deploy);
	warInit.on(auto(), war);
	goldInit.on(auto(), gold);
	ageInit.on(auto(), age);

	StateBuilder draftInit = draft.auto(GameConst.INIT);
	StateBuilder cur = draft.orthogonal(GameConst.draftStep(0));
	draftInit.on(auto(), cur);
	for (int i = 1; i <= 4; ++i) {
	    StateBuilder next = deployInit;
	    if (i < 4)
		next = draft.orthogonal(GameConst.draftStep(i));

	    createWaiting(cur, next);
	    cur = next;
	}

	createWaitingDeploy(deploy, warInit);
	createWaiting(war, goldInit);
	createWaiting(gold, build);
	createWaiting(build, ageInit);
	createWaitingAge(age, endOfTurn);

	endOfTurn.on(choice(GameConst.NEXT_TURN_CHOICE, prepare).otherwise(end));

	return builder.build();
    }

}
