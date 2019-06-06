package fr.keyser.n.fsm.listener.timeout;

import fr.keyser.n.fsm.automat.TimeOut;

@FunctionalInterface
public interface TimeoutScheduler {

    RegisteredTimeOut schedule(TimeOut timedout);
}
