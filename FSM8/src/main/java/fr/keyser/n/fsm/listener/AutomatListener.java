package fr.keyser.n.fsm.listener;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;
import fr.keyser.n.fsm.Transition;

public interface AutomatListener {

    public default void leaving(InstanceState instance, State leaved, Event event) {
    }

    public default void following(InstanceState instance, Transition transition) {
    }

    public default void entering(InstanceState instance, State entered, Event event) {
    }

    public default boolean guard(InstanceState instance, Transition transition) {
	return true;
    }

    public default boolean guard(InstanceState instance, Event event) {
	return true;
    }

    public default void reaching(InstanceState instance, State reached, StateType type, Event event) {
    }

    public default void terminating(InstanceState instance) {
    }

    public default void starting(InstanceState instance) {
    }
}
