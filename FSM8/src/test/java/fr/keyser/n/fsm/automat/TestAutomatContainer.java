package fr.keyser.n.fsm.automat;

import static fr.keyser.n.fsm.automat.ChoiceTransitionSourceBuilder.choice;
import static fr.keyser.n.fsm.automat.StateBuilder.join;
import static fr.keyser.n.fsm.automat.StateBuilder.timeout;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.EventReceiver;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.automat.TestingTimeoutScheduler.TestingTimeout;

public class TestAutomatContainer {

    private static final String SECOND_CHOICE = "2";
    private static final String FIRST_CHOICE = "1";

    @Test
    public void testOrtho() {
	AutomatBuilder builder = new AutomatBuilder();
	StateBuilder ortho = builder.orthogonal("ortho");
	StateBuilder s1 = ortho.state("s1");

	StateBuilder s2 = ortho.state("s2");
	StateBuilder inner = s2.choice("inner");
	StateBuilder outer = s2.state("outer");

	StateBuilder join = ortho.joining("join");
	StateBuilder terminal = builder.terminal("terminal");
	ortho.on(join(), terminal);

	s1.on(timeout(), join);
	inner.on(choice(FIRST_CHOICE, s1).or(SECOND_CHOICE, outer).otherwise(join));
	outer.onEvent("progress", join);

	TestingTimeoutScheduler scheduler = new TestingTimeoutScheduler();

	Automat build = builder.build();
	System.out.println(build);

	AutomatContainerBuilder cib = new AutomatContainerBuilder(build)
	        .timeoutScheduler(scheduler);
	EventReceiver er = cib.getReceiver();

	cib.state(new State("ortho", "s1"))
	        .timeout(() -> Duration.ofSeconds(3));
	cib.state(new State("ortho", "s2", "inner"))
	        .with(FIRST_CHOICE, () -> false)
	        .with(SECOND_CHOICE, () -> true);
	cib.state(new State("ortho", "s2", "outer"))
	        .entry(id -> er.receive(Event.event("progress").id(id)));
	cib.state(new State("terminal")).entry(() -> System.out.println("termine!"));

	AutomatContainer ci = cib.build();
	ci.start();

	Optional<TestingTimeout> first = scheduler.getTimeouts().stream().findFirst();
	first.ifPresent(it -> it.fire(ci));
    }

}
