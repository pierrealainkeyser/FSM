package fr.keyser.n.fsm;

import static fr.keyser.n.fsm.automat.ChoiceTransitionSourceBuilder.choice;
import static fr.keyser.n.fsm.automat.StateBuilder.join;

import org.junit.jupiter.api.Test;

import fr.keyser.n.fsm.automat.AutomatBuilder;
import fr.keyser.n.fsm.automat.StateBuilder;

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

	createWaiting(deploy, war);
	createWaiting(war, gold);
	createWaiting(gold, build);
	createWaiting(build, age);
	createWaiting(age, endOfTurn);

	endOfTurn.on(choice("nextTurn", turn).otherwise(end));

	System.out.println(builder.build());

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
