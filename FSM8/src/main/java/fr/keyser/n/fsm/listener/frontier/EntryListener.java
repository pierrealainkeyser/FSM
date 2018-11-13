package fr.keyser.n.fsm.listener.frontier;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.State;

@FunctionalInterface
public interface EntryListener {

    public void handle(InstanceId id, State state, Event event);
}
