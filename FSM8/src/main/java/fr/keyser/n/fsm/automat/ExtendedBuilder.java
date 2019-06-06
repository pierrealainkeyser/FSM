package fr.keyser.n.fsm.automat;

import fr.keyser.n.fsm.StateType;

public interface ExtendedBuilder {

    StateBuilder state(String state, StateType type);

    public default StateBuilder auto(String state) {
	return state(state, StateType.AUTO);
    }

    public default StateBuilder state(String state) {
	return state(state, StateType.PLAIN);
    }

    public default StateBuilder choice(String state) {
	return state(state, StateType.CHOICE);
    }

    public default StateBuilder orthogonal(String state) {
	return state(state, StateType.ORTHOGONAL);
    }

    public default StateBuilder terminal(String state) {
	return state(state, StateType.TERMINAL);
    }

}