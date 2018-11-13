package fr.keyser.n.fsm.listener;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;
import fr.keyser.n.fsm.Transition;

public interface AutomatListener {

    public default void leaving(InstanceId id, State leaved, Event event) {
    }

    public default void following(InstanceId id, Transition transition) {
    }

    public default void entering(InstanceId id, State entered, Event event) {
    }

    public default boolean guard(InstanceId id, Transition transition) {
	return true;
    }

    public default boolean guard(InstanceId id, Event event) {
	return true;
    }

    public default void reaching(InstanceId id, State reached, StateType type) {
    }

    public default void terminating(InstanceState instance) {
    }

    public default void starting(InstanceState instance) {
    }
}
