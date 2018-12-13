package fr.keyser.n.fsm;

import static fr.keyser.n.fsm.automat.ChoiceTransitionSourceBuilder.choice;
import static fr.keyser.n.fsm.automat.StateBuilder.join;

import org.junit.jupiter.api.Test;

import fr.keyser.n.fsm.automat.Automat;
import fr.keyser.n.fsm.automat.AutomatBuilder;
import fr.keyser.n.fsm.automat.StateBuilder;
import fr.keyser.n.fsm.container.AutomatContainerBuilder;

public class TestAutomatBuilder {
    @Test
    public void testPT() {
	AutomatBuilder builder = new AutomatBuilder();

	StateBuilder turn = builder.state("turn");

	StateBuilder draft = turn.state("draft");
	StateBuilder deploy = turn.orthogonal("deploy");
	StateBuilder war = turn.orthogonal("war");
	StateBuilder gold = turn.orthogonal("gold");
	StateBuilder build = turn.orthogonal("build");
	StateBuilder age = turn.orthogonal("age");
	StateBuilder endOfTurn = builder.choice("endOfTurn");
	StateBuilder end = builder.terminal("ended");

	StateBuilder cur = draft.orthogonal("D0");
	for (int i = 1; i <= 4; ++i) {
	    StateBuilder next = deploy;
	    if (i < 4)
		next = draft.orthogonal("D" + i);

	    createWaiting(cur, next);
	    cur = next;
	}

	createWaitingInput(deploy, war);
	createWaiting(war, gold);
	createWaiting(gold, build);
	createWaiting(build, age);
	createWaiting(age, endOfTurn);

	endOfTurn.on(choice("nextTurn", turn).otherwise(end));

	Automat automat = builder.build();
	System.out.println(automat);

	AutomatContainerBuilder cib = new AutomatContainerBuilder(automat);
	cib.build().start();

    }

    private void createWaitingInput(StateBuilder parent, StateBuilder dest) {
	StateBuilder done = parent.joining("done");
	for (int i = 0; i < 3; ++i) {
	    StateBuilder base = parent.state("P" + i);
	    StateBuilder check = base.choice("CheckInput");
	    StateBuilder waitingInput = base.state("WaitingInput");
	    StateBuilder waiting = base.state("Waiting");

	    check.on(choice("hasInput", waitingInput)
	            .otherwise(waiting));

	    waitingInput.onEvent("done", done);
	    waitingInput.on(StateBuilder.timeout(), done);
	    
	    waiting.onEvent("done", done);
	    waiting.on(StateBuilder.timeout(), done);
	}
	parent.on(join(), dest);
    }

    private void createWaiting(StateBuilder parent, StateBuilder dest) {
	StateBuilder done = parent.joining("done");
	for (int i = 0; i < 3; ++i) {
	    StateBuilder p = parent.state("P" + i);
	    p.onEvent("done", done);
	    p.on(StateBuilder.timeout(), done);
	}
	parent.on(join(), dest);
    }

}
