package fr.keyser.n.fsm;

import static fr.keyser.n.fsm.automat.ChoiceTransitionSourceBuilder.choice;
import static fr.keyser.n.fsm.automat.StateBuilder.join;

import org.junit.jupiter.api.Test;

import fr.keyser.n.fsm.automat.Automat;
import fr.keyser.n.fsm.automat.AutomatBuilder;
import fr.keyser.n.fsm.automat.AutomatContainer;
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

	createWaitingDeploy(deploy, war);
	createWaiting(war, gold);
	createWaiting(gold, build);
	createWaiting(build, age);
	createWaitingAge(age, endOfTurn);

	endOfTurn.on(choice("nextTurn", turn).otherwise(end));

	Automat automat = builder.build();
	System.out.println(automat);

	AutomatContainerBuilder acb = new AutomatContainerBuilder(automat);
	acb.state(new State("turn", "draft")).entry(() -> System.out.println("distribute"));
	AutomatContainer ac = acb.build();
	ac.start();

	ac.receive(Event.event("done").id(new InstanceId("1")));
	ac.receive(Event.event("done").id(new InstanceId("2")));
	ac.receive(Event.event("done").id(new InstanceId("3")));

    }

    private void createWaitingDeploy(StateBuilder parent, StateBuilder dest) {
	StateBuilder done = parent.joining("Done");
	for (int i = 0; i < 3; ++i) {
	    StateBuilder base = parent.state("P" + i);
	    StateBuilder play = base.state("Play");
	    StateBuilder check = base.choice("CheckInput");
	    StateBuilder waitingInput = base.state("WaitingInput");
	    StateBuilder hasMoreInput = base.choice("HasMoreInput");

	    play.onEvent("done", check);
	    play.on(StateBuilder.timeout(), check);

	    check.on(choice("hasInput", waitingInput)
	            .otherwise(done));

	    waitingInput.onEvent("done", hasMoreInput);
	    waitingInput.on(StateBuilder.timeout(), done);

	    hasMoreInput.on(choice("hasInput", waitingInput)
	            .otherwise(done));

	}
	parent.on(join(), dest);
    }

    private void createWaitingAge(StateBuilder parent, StateBuilder dest) {
	StateBuilder done = parent.joining("Done");
	for (int i = 0; i < 3; ++i) {
	    StateBuilder base = parent.state("P" + i);
	    StateBuilder check = base.choice("CheckInput");
	    StateBuilder waitingInput = base.state("WaitingInput");
	    StateBuilder hasMoreInput = base.choice("HasMoreInput");
	    StateBuilder waiting = base.state("Waiting");

	    check.on(choice("hasInput", waitingInput)
	            .otherwise(waiting));

	    hasMoreInput.on(choice("hasInput", waitingInput)
	            .otherwise(done));

	    waitingInput.onEvent("done", hasMoreInput);
	    waitingInput.on(StateBuilder.timeout(), done);

	    waiting.onEvent("done", done);
	    waiting.on(StateBuilder.timeout(), done);
	}
	parent.on(join(), dest);
    }

    private void createWaiting(StateBuilder parent, StateBuilder dest) {
	StateBuilder done = parent.joining("Done");
	for (int i = 0; i < 3; ++i) {
	    StateBuilder p = parent.state("P" + i);
	    p.onEvent("done", done);
	    p.on(StateBuilder.timeout(), done);
	}
	parent.on(join(), dest);
    }

}
