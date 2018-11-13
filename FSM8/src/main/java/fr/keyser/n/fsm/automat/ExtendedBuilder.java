package fr.keyser.n.fsm.automat;

import fr.keyser.n.fsm.StateType;

public interface ExtendedBuilder<T> {

    T state(String state, StateType type);

    public default T state(String state) {
	return state(state, StateType.PLAIN);
    }

    public default T choice(String state) {
	return state(state, StateType.CHOICE);
    }

    public default T orthogonal(String state) {
	return state(state, StateType.ORTHOGONAL);
    }

    public default T terminal(String state) {
	return state(state, StateType.TERMINAL);
    }

}