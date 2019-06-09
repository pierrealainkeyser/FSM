package fr.keyser.pt2.automat;

import fr.keyser.n.fsm.State;

class GameConst {

    private GameConst() {
	// helper classe
    }

    static final String INIT = "init";

    static final String PLAY = "Play";
    static final String INIT_PLAY = init(PLAY);

    static final String AGE = "age";
    static final String INIT_AGE = init(AGE);

    static final String BUILD = "build";

    static final String GOLD = "gold";
    static final String INIT_GOLD = init(GOLD);

    static final String WAR = "war";
    static final String INIT_WAR = init(WAR);

    static final String DEPLOY = "deploy";
    static final String INIT_DEPLOY = init(DEPLOY);

    static final String DRAFT = "draft";

    static final String END_OF_TURN = "endOfTurn";

    static final String TURN = "turn";
    static final String INIT_TURN = init(TURN);

    static final String NEXT_TURN_CHOICE = "nextTurn";
    static final String DONE_STATE = "Done";
    static final String DONE = "done";
    static final String WAITING = "Waiting";
    static final String HAS_MORE_INPUT = "HasMoreInput";
    static final String WAITING_INPUT = "WaitingInput";
    static final String CHECK_INPUT = "CheckInput";
    static final String HAS_INPUT = "hasInput";

    private final static String init(String state) {
	return state + "-" + INIT;
    }

    static String forPlayer(int player) {
	return "P" + player;
    }

    static State forPlayer(State state, int player) {
	return state.sub(forPlayer(player));
    }

    static String draftStep(int step) {
	return "D" + step;
    }

}
