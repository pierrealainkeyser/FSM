package fr.keyser.n.fsm.listener.frontier;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;

@FunctionalInterface
public interface EntryListener {

    public void handle(InstanceState id, State state, Event event);
}
