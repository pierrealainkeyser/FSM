package fr.keyser.n.fsm.listener.timeout;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.automat.TimeOut;
import fr.keyser.n.fsm.listener.AutomatListener;
import fr.keyser.n.fsm.listener.DelegatedAutomatListener;

public class TimeOutAutomatListener extends DelegatedAutomatListener {

    private static class BoundTimeOut {
	public BoundTimeOut(TimeOut timeout, RegisteredTimeOut register) {
	    this.timeout = timeout;
	    this.register = register;
	}

	private final TimeOut timeout;

	private final RegisteredTimeOut register;

	public boolean isSameTimer(long timer) {
	    return timeout.getTimer() == timer;
	}

	public boolean isSameState(State state) {
	    return timeout.getState().equals(state);
	}

	public void cancel() {
	    register.cancel();
	}
    }

    private final Map<State, Supplier<Duration>> timeouts = new LinkedHashMap<>();

    private final Map<InstanceId, BoundTimeOut> registeredTimeout = new HashMap<>();

    private long nextTimeout = 0;

    private final TimeoutScheduler scheduler;

    public TimeOutAutomatListener(AutomatListener listener, TimeoutScheduler scheduler) {
	super(listener);
	this.scheduler = scheduler;
    }

    public TimeOutAutomatListener timeout(State state, Supplier<Duration> supplier) {
	timeouts.put(state, supplier);
	return this;
    }

    private void schedule(TimeOut timed) {
	registeredTimeout.put(timed.getId(), new BoundTimeOut(timed, scheduler.schedule(timed)));
    }

    @Override
    public boolean guard(InstanceState state, Event event) {
	if (event instanceof TimeOut) {
	    TimeOut incomming = (TimeOut) event;

	    // same timer
	    BoundTimeOut current = registeredTimeout.get(state.getId());
	    return current != null && current.isSameTimer(incomming.getTimer());
	}

	return super.guard(state, event);
    }

    @Override
    public void terminating(InstanceState instance) {
	InstanceId id = instance.getId();
	BoundTimeOut timedOut = registeredTimeout.get(id);
	if (timedOut != null) {
	    removeTimedOut(id);
	}

	super.terminating(instance);
    }

    @Override
    public void entering(InstanceState state, State entered, Event event) {
	Supplier<Duration> d = timeouts.get(entered);
	if (d != null) {
	    schedule(TimeOut.timeout(state.getId(), entered, ++nextTimeout, d.get()));
	}
	super.entering(state, entered, event);
    }

    private void removeTimedOut(InstanceId id) {
	BoundTimeOut removed = registeredTimeout.remove(id);
	if (removed != null) {
	    removed.cancel();
	}
    }

    @Override
    public void leaving(InstanceState state, State leaved, Event event) {
	InstanceId id = state.getId();
	BoundTimeOut timedOut = registeredTimeout.get(id);
	if (timedOut != null && timedOut.isSameState(leaved)) {
	    removeTimedOut(id);
	}
	super.leaving(state, leaved, event);
    }
}
