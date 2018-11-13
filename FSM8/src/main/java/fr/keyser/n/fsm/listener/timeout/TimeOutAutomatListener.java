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
import fr.keyser.n.fsm.listener.AutomatListener;
import fr.keyser.n.fsm.listener.DelegatedAutomatListener;

public class TimeOutAutomatListener extends DelegatedAutomatListener {

    private final Map<State, Supplier<Duration>> timeouts = new LinkedHashMap<>();

    private final Map<InstanceId, RegisteredTimeOut> registeredTimeout = new HashMap<>();

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
	registeredTimeout.put(timed.getId(), scheduler.schedule(timed));
    }

    @Override
    public boolean guard(InstanceId id, Event event) {
	if (event instanceof TimeOut) {
	    TimeOut incomming = (TimeOut) event;

	    // same timer
	    RegisteredTimeOut current = registeredTimeout.get(id);
	    return current != null && current.isSameTimer(incomming.getTimer());
	}

	return super.guard(id, event);
    }

    @Override
    public void terminating(InstanceState instance) {
	InstanceId id = instance.getId();
	RegisteredTimeOut timedOut = registeredTimeout.get(id);
	if (timedOut != null) {
	    removeTimedOut(id);
	}

	super.terminating(instance);
    }

    @Override
    public void entering(InstanceId id, State entered, Event event) {
	Supplier<Duration> d = timeouts.get(entered);
	if (d != null) {
	    schedule(TimeOut.timeout(id, entered, ++nextTimeout, d.get()));
	}
	super.entering(id, entered, event);
    }

    private void removeTimedOut(InstanceId id) {
	RegisteredTimeOut removed = registeredTimeout.remove(id);
	if (removed != null) {
	    removed.cancel();
	}
    }

    @Override
    public void leaving(InstanceId id, State leaved, Event event) {
	RegisteredTimeOut timedOut = registeredTimeout.get(id);
	if (timedOut != null && timedOut.isSameState(leaved)) {
	    removeTimedOut(id);
	}
	super.leaving(id, leaved, event);
    }
}
