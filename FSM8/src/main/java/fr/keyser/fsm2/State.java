package fr.keyser.fsm2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class State<S> {

    private final List<S> states;

    public State(List<S> states) {
	if (states.isEmpty())
	    throw new IllegalArgumentException("empty states not allowed");
	this.states = Collections.unmodifiableList(states);
    }

    @SafeVarargs
    public State(S... states) {
	this(Arrays.asList(states));
    }

    /**
     * Création d'un sous état
     * 
     * @param sub
     * @return
     */
    public State<S> subState(S sub) {
	ArrayList<S> st = new ArrayList<>(states);
	st.add(sub);
	return new State<>(st);
    }

    public boolean isHierarchical() {
	return states.size() > 1;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((states == null) ? 0 : states.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	State<?> other = (State<?>) obj;
	if (states == null) {
	    if (other.states != null)
		return false;
	} else if (!states.equals(other.states))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "State" + states;
    }
}
