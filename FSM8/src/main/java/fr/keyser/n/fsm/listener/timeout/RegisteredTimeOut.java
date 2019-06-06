package fr.keyser.n.fsm.listener.timeout;

@FunctionalInterface
public interface RegisteredTimeOut {

    public void cancel();
}
