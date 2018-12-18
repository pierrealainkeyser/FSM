package fr.keyser.n.fsm.automat;

import java.util.Optional;
import java.util.stream.Stream;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.EventProcessingStatus;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;
import fr.keyser.n.fsm.Transition;
import fr.keyser.n.fsm.listener.AutomatListener;

class AutomatInstance {

    private final Automat automat;

    private State current;

    private final InstanceId parentId;

    private final InstanceId id;

    private final AutomatListener listener;

    AutomatInstance(InstanceId parentId, Automat automat, State current, InstanceId id, AutomatListener listener) {
	this.parentId = parentId;
	this.automat = automat;
	this.current = current;
	this.id = id;
	this.listener = listener;
    }

    InstanceState getInstanceState() {
	return new InstanceState(id, parentId, current);
    }

    private boolean guard(Transition transition) {
	return listener.guard(getInstanceState(), transition);
    }

    void receive(EventProcessingStatus status, Event event) {
	Stream<Transition> transitions = automat.findTransitions(current, event);

	Optional<Transition> transition = transitions.filter(this::guard).findFirst();
	if (transition.isPresent()) {
	    Transition t = transition.get();

	    follow(status, t);
	}
    }

    private void follow(EventProcessingStatus status, Transition t) {
	InstanceState state = getInstanceState();
	t.leaving().forEach(l -> listener.leaving(state, l, t.getEvent()));
	listener.following(state, t);
	t.entering().forEach(e -> listener.entering(state, e, t.getEvent()));

	current = t.getDestination();
	reachCurrentState();
    }

    void start() {
	InstanceState state = getInstanceState();
	if (parentId == null) {
	    current.states().forEach(s -> listener.entering(state, s, Started.INSTANCE));
	} else
	    listener.entering(state, current, Started.INSTANCE);

	reachCurrentState();
    }

    private void reachCurrentState() {
	StateType type = automat.type(current);
	listener.reaching(getInstanceState(), current, type);
    }

    InstanceId getId() {
	return id;
    }

    State getCurrent() {
	return current;
    }

    InstanceId getParentId() {
	return parentId;
    }
}
