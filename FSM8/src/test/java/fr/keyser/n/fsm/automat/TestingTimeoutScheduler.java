package fr.keyser.n.fsm.automat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.keyser.n.fsm.EventReceiver;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.listener.timeout.RegisteredTimeOut;
import fr.keyser.n.fsm.listener.timeout.TimeoutScheduler;

public class TestingTimeoutScheduler implements TimeoutScheduler {

    public class TestingTimeout implements RegisteredTimeOut {

	private final TimeOut timeout;

	public TestingTimeout(TimeOut timeout) {
	    this.timeout = timeout;
	    timeouts.put(timeout.getId(), this);
	}

	public void fire(EventReceiver receiver) {
	    receiver.receive(timeout);
	}

	@Override
	public void cancel() {
	    timeouts.remove(timeout.getId());

	}
    }

    private Map<InstanceId, TestingTimeout> timeouts = new HashMap<>();

    @Override
    public RegisteredTimeOut schedule(TimeOut timedout) {
	return new TestingTimeout(timedout);

    }

    public Collection<TestingTimeout> getTimeouts() {
	return timeouts.values();
    }

}
