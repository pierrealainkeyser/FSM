package fr.keyser.fsm;

import static fr.keyser.fsm.Event.create;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import fr.keyser.fsm.DelayedEventConsumer;
import fr.keyser.fsm.Event;
import fr.keyser.fsm.RoutingStatus;
import fr.keyser.fsm.State;
import fr.keyser.fsm.StateMachine;
import fr.keyser.fsm.StateMachineBuilder;
import fr.keyser.fsm.StateMachineBuilder.StateBuilder;

public class TestStateMachineBuilder {

    /**
     * Exemple pris de https://en.wikipedia.org/wiki/Finite-state_machine
     */
    @Test
    public void turnstile() {
	StateMachineBuilder<String, String> builder = new StateMachineBuilder<>();
	DelayedEventConsumer<String, String> ec = builder.eventConsummer();

	StateBuilder<String, String> locked = builder.state("locked")
	        .onExit(() -> System.out.println("exit locked"))
	        .onEntry(() -> System.out.println("entry locked"));
	StateBuilder<String, String> unlocked = builder.state("unlocked")
	        .onEntry(() -> System.out.println("entry unlocked"))
	        .onExit(() -> System.out.println("exit unlocked"))
	        .onEntry(() -> ec.push("push"));

	locked.selfTransition("push").onTransition(() -> System.out.println("locked -> push ignored "));
	locked.transition("coin", unlocked).onTransition(() -> System.out.println("coin -> unlocked"));

	unlocked.selfTransition("coin");
	unlocked.transition("push", locked).onTransition(() -> System.out.println("push -> locked"));

	StateMachine<String, String> machine = builder.build();
	State<String> lockedState = new State<>("locked");

	Assert.assertEquals(lockedState, machine.getCurrent());
	machine.push("push");
	Assert.assertEquals(lockedState, machine.getCurrent());

	CompletionStage<RoutingStatus<String, String>> last = machine.push("coin");
	Assert.assertEquals(lockedState, machine.getCurrent());
	Assert.assertEquals(lockedState, last.toCompletableFuture().join().getTo());
    }

    private static class TimerModel {
	private int value;

	private boolean armed;

	private int numlock;

	private String timeoutEvent;

	public static <S> Event.Builder<S> timer(Event.Builder<S> b, int value, String event) {
	    return b.put("timer", value).put("timeoutEvent", event);
	}

	public void setupTimer(Event<String> event) {
	    value = (int) event.getOrDefault("timer", 5);
	    timeoutEvent = (String) event.get("timeoutEvent");
	}

	public boolean checkTimeout(Event<String> event) {
	    Object object = event.get("numlock");
	    if (object instanceof Integer)
		return ((Integer) object).intValue() == numlock;
	    else
		return false;
	}

	public void disarmTimer() {
	    armed = false;
	}

	public void unsetupTimer() {
	    value = 0;
	    timeoutEvent = null;
	}

	public void armTimer() {
	    ++numlock;
	    armed = true;
	}
    }

    @Test
    public void timeout() {
	StateMachineBuilder<String, String> builder = new StateMachineBuilder<>();

	TimerModel model = new TimerModel();

	AtomicInteger fireCount = new AtomicInteger(0);

	StateBuilder<String, String> idle = builder.state("idle")
	        .onEntry(model::unsetupTimer);
	StateBuilder<String, String> idleOff = idle.sub("off");
	StateBuilder<String, String> idleOn = idle.sub("on");

	StateBuilder<String, String> armed = builder.state("armed");
	StateBuilder<String, String> armedOff = armed.sub("off");
	StateBuilder<String, String> armedOn = armed.sub("on")
	        .onEntry(model::armTimer)
	        .onExit(model::disarmTimer);

	idleOn.transition("arm", armedOn).onTransition(model::setupTimer);
	armedOn.transition("disarm", idleOn);
	armedOn.transition("timeout", idleOn).guard(model::checkTimeout).onTransition(() -> {
	    fireCount.incrementAndGet();
	});

	idleOff.transition("arm", armedOff).onTransition(model::setupTimer);
	armedOff.transition("disarm", idleOff);
	armedOff.transition("timeout", idleOff).guard(model::checkTimeout);

	idleOn.transition("offline", idleOff);
	armedOn.transition("offline", armedOff);

	idleOff.transition("online", idleOn);
	armedOff.transition("online", armedOn);

	StateMachine<String, String> machine = builder.build();
	machine.enterInitialState();
	Assert.assertEquals(new State<>("idle", "off"), machine.getCurrent());

	machine.push("online");
	Assert.assertEquals(new State<>("idle", "on"), machine.getCurrent());

	machine.push(TimerModel.timer(create("arm"), 10, "go"));
	Assert.assertTrue(model.armed);
	Assert.assertEquals(10, model.value);
	Assert.assertEquals("go", model.timeoutEvent);

	machine.push(create("timeout").put("numlock", 1));
	Assert.assertFalse(model.armed);
	Assert.assertEquals(1, fireCount.get());

	machine.push("offline");
	machine.push(TimerModel.timer(create("arm"), 15, "timeout"));

	Assert.assertFalse(model.armed);
	Assert.assertEquals(15, model.value);
	Assert.assertEquals("timeout", model.timeoutEvent);

	machine.push("online");
	Assert.assertTrue(model.armed);
	Assert.assertEquals(15, model.value);

	machine.push("offline");
	machine.push("timeout");
	Assert.assertFalse(model.armed);
	Assert.assertEquals(1, fireCount.get());
    }
}
