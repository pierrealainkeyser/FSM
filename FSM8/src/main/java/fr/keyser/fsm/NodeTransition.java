package fr.keyser.fsm;

import java.util.List;

class NodeTransition<S, E> {

    private final State<S> destination;

    private final List<OnTransitionAction<S, E>> onTransition;

    private final List<TransitionGuard<E>> guards;

    NodeTransition(State<S> destination, List<OnTransitionAction<S, E>> onTransition, List<TransitionGuard<E>> guards) {
	this.destination = destination;
	this.onTransition = onTransition;
	this.guards = guards;
    }

    public boolean validate(Event<E> event) throws Exception {
	boolean valid = true;
	for (TransitionGuard<E> g : guards) {
	    valid = g.validate(event);
	    if (!valid)
		break;
	}
	return valid;
    }

    public void fireTransition(State<S> from, Event<E> event, State<S> to) throws Exception {
	if (onTransition != null) {
	    for (OnTransitionAction<S, E> o : onTransition)
		o.onTransition(from, event, to);
	}
    }

    @Override
    public String toString() {
	return "->" + destination;
    }

    public State<S> computeDestination(State<S> current) {
	return destination == null ? current : destination;
    }

}
