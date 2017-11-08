package fr.keyser.fsm;

import java.util.Map;
import java.util.Optional;

import fr.keyser.fsm.exception.FSMNullEventException;

class State<S, E, C> {

    private final OnEnterAction<S, E, C> onEnter;

    private final OnExitAction<S, E, C> onExit;

    private final S state;

    private final Map<E, Transition<S, E, C>> transitions;

    private final boolean terminal;

    public State(S state, OnEnterAction<S, E, C> onEnter, OnExitAction<S, E, C> onExit,
	    Map<E, Transition<S, E, C>> transitions, boolean terminal) {
	this.state = state;
	this.onEnter = onEnter;
	this.onExit = onExit;
	this.transitions = transitions;
	this.terminal = terminal;
    }

    void visit(FSMVisitor<S, E> visitor) {
	for (Transition<S, E, C> t : transitions.values())
	    visitor.transition(state, t.getEvent(), t.getOnTransition(), t.getDestination());
    }

    Optional<Transition<S, E, C>> lookup(E event) throws FSMException {
	if (event == null)
	    throw new FSMNullEventException();

	if (transitions.containsKey(event))
	    return Optional.of(transitions.get(event));
	else
	    return Optional.empty();
    }

    OnEnterAction<S, E, C> getOnEnter() {
	return onEnter;
    }

    OnExitAction<S, E, C> getOnExit() {
	return onExit;
    }

    boolean isTerminal() {
	return terminal;
    }

    S getState() {
	return state;
    }
}
