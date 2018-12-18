package fr.keyser.n.fsm.listener;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;
import fr.keyser.n.fsm.Transition;

public interface AutomatListener {

    public default void leaving(InstanceState id, State leaved, Event event) {
    }

    public default void following(InstanceState id, Transition transition) {
    }

    public default void entering(InstanceState id, State entered, Event event) {
    }

    public default boolean guard(InstanceState id, Transition transition) {
	return true;
    }

    public default boolean guard(InstanceState id, Event event) {
	return true;
    }

    public default void reaching(InstanceState id, State reached, StateType type) {
    }

    public default void terminating(InstanceState instance) {
    }

    public default void starting(InstanceState instance) {
    }
}
