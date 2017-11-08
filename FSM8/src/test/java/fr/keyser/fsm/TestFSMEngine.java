package fr.keyser.fsm;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class TestFSMEngine {

    /**
     * Création d'une FSM à 2 états, qui navigue automatiquement de l'état initial à
     * l'état final en appelant les transitions
     */
    @Test
    public void simple() {

	String[] leaving = new String[1];
	@SuppressWarnings("unchecked")
	FSMEvent<String>[] event = new FSMEvent[1];

	FSMInstanceKey key = new FSMInstanceKey(new FSMKey("simple"), "0");

	FSMBuilder<String, String, Object> builder = new FSMBuilder<>();
	builder.instanceKey(() -> key)
		.state("initial").onEnter((i, e) -> i.sendEvent("next", 1))
		.transition("next", "terminal", (i, e) -> event[0] = e)
		.and()
		.state("terminal").terminal();

	// on peut modifier l'état
	builder.state("initial").onExit((i, e) -> leaving[0] = i.getCurrentState());

	FSM<String, String, Object> fsm = builder.build();
	FSMInstance<String, String, Object> instance = fsm.start("myself");
	Assert.assertTrue("L'instance est terminée", instance.isDone());
	Assert.assertEquals("L'état à changer", "terminal", instance.getCurrentState());
	Assert.assertEquals("L'événement onExit est appelé", "initial", leaving[0]);
	Assert.assertEquals("L'événement onTransition est appelé", new FSMEvent<>("next", 1), event[0]);
	Assert.assertEquals(1l, instance.getTransitionCount());
	Assert.assertEquals(key, instance.getKey());
    }

    /**
     * Exemple pris de https://en.wikipedia.org/wiki/Finite-state_machine
     */
    @Test
    public void turnstile() {
	FSMBuilder<String, String, AtomicBoolean> builder = new FSMBuilder<>();
	builder.state("locked").onEnter((i, e) -> i.getContext().set(true))
		.transition("push", "locked")
		.transition("coin", "unlocked");
	builder.state("unlocked").onEnter((i, e) -> i.getContext().set(false))
		.transition("push", "locked")
		.transition("coin", "unlocked");

	AtomicBoolean context = new AtomicBoolean(false);
	FSMInstance<String, String, AtomicBoolean> instance = builder.build().start(context);
	Assert.assertTrue("Devrait être verrouillé", context.get());
	instance.sendEvent("push");
	Assert.assertTrue("Devrait être verrouillé", context.get());
	instance.sendEvent("coin");
	Assert.assertFalse("Devrait être déverrouillé", context.get());
	instance.sendEvent("coin");
	Assert.assertFalse("Devrait être déverrouillé", context.get());
	instance.sendEvent("push");
	Assert.assertTrue("Devrait être verrouillé", context.get());
    }

    @Test
    public void automaticDoor() {
	FSMBuilder<String, String, AtomicReference<String>> builder = new FSMBuilder<>();

	builder.state("closed").onEnter((i, e) -> i.getContext().set("."))
		.transition("open", "opening");

	OnTransitionAction<String, String, AtomicReference<String>> sensor = (i, e) -> {
	    float progress = (float) e.getArgs().get(0);
	    if (progress <= 0f)
		i.sendEvent("sensor_closed");
	    else if (progress >= 1f)
		i.sendEvent("sensor_opened");
	};

	builder.state("opening").onEnter((i, e) -> i.getContext().set("opening"))
		.self("sensor", sensor)
		.transition("sensor_opened", "open")
		.transition("close", "closing");

	builder.state("open").onEnter((i, e) -> i.getContext().set("."))
		.transition("close", "closing");

	builder.state("closing").onEnter((i, e) -> i.getContext().set("closing"))
		.self("sensor", sensor)
		.transition("sensor_closed", "closed")
		.transition("open", "opening");

	AtomicReference<String> context = new AtomicReference<String>(null);
	FSMInstance<String, String, AtomicReference<String>> instance = builder.build().start(context);

	instance.sendEvent("open");
	Assert.assertEquals("opening", context.get());
	instance.sendEvent("sensor", .5f);
	Assert.assertEquals("opening", context.get());
	instance.sendEvent("sensor", 1f);
	Assert.assertEquals(".", context.get());
	instance.sendEvent("close");
	Assert.assertEquals("closing", context.get());
	instance.sendEvent("open");
	Assert.assertEquals("opening", context.get());
	instance.sendEvent("close");
	Assert.assertEquals("closing", context.get());
	instance.sendEvent("sensor", .5f);
	instance.sendEvent("sensor", 0f);
	Assert.assertEquals(".", context.get());
	Assert.assertEquals("closed", instance.getCurrentState());
    }
}
