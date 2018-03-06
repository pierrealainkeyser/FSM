package fr.keyser.fsm;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class TimedStateBuilder<E> {

    private Future<?> currentTimeout;

    private final Supplier<Duration> delaySupplier;

    private final DelayedEventConsumer<?, E> entry;

    private final E event;

    private int numlock;

    private final ScheduledExecutorService service;

    public TimedStateBuilder(ScheduledExecutorService service, DelayedEventConsumer<?, E> entry, E event,
            Supplier<Duration> delaySupplier) {
	this.service = service;
	this.entry = entry;
	this.event = event;
	this.delaySupplier = delaySupplier;
    }

    private void arm() {
	disarm();
	++numlock;
	Event<E> toReturn = Event.create(event).put("numlock", numlock).build();

	Duration delay = delaySupplier.get();
	Runnable onExternalTimeout = () -> entry.push(toReturn);
	currentTimeout = service.schedule(onExternalTimeout, delay.toMillis(), TimeUnit.MILLISECONDS);
    }

    private void disarm() {
	if (currentTimeout != null) {
	    currentTimeout.cancel(false);
	    currentTimeout = null;
	}
    }

    public TimedStateBuilder<E> state(StateMachineBuilder.StateBuilder<?, E> state) {
	state.onEntry(this::arm);
	state.onExit(this::disarm);
	state.guard(this::guard);
	return this;
    }

    public TimedStateBuilder<E> transition(StateMachineBuilder.TransitionBuilder<?, E> transition) {
	transition.guard(this::guard);
	return this;
    }

    private boolean guard(Event<E> e) {
	if (!event.equals(e.getValue()))
	    return true;

	return numlock == (Integer) e.get("numlock");
    }
}
