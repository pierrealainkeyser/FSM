package fr.keyser.n.fsm.listener.timeout;

import fr.keyser.n.fsm.State;

public interface RegisteredTimeOut {

    public boolean isSameTimer(long timer);

    public boolean isSameState(State state);

    public void cancel();
}
