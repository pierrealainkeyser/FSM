package fr.keyser.n.fsm.listener.timeout;

public class LazyTimeoutScheduler implements TimeoutScheduler {

    private TimeoutScheduler timeoutScheduler;

    public void setTimeoutScheduler(TimeoutScheduler timeoutScheduler) {
	this.timeoutScheduler = timeoutScheduler;
    }

    @Override
    public RegisteredTimeOut schedule(TimeOut timedout) {
	if (timeoutScheduler == null)
	    throw new IllegalStateException("No scheduler is registered");
	return timeoutScheduler.schedule(timedout);
    }

}
