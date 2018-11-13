package fr.keyser.n.fsm.listener.timeout;

@FunctionalInterface
public interface TimeoutScheduler {

    RegisteredTimeOut schedule(TimeOut timedout);
}
