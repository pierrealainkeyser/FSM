package fr.keyser.n.fsm.automat;

import java.util.HashMap;
import java.util.Map;
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

    private final Map<String, Object> props = new HashMap<>();

    AutomatInstance(InstanceId parentId, Automat automat, State current, InstanceId id, AutomatListener listener) {
	this.parentId = parentId;
	this.automat = automat;
	this.current = current;
	this.id = id;
	this.listener = listener;
    }

    InstanceState getInstanceState() {
	return new InstanceState(id, parentId, current, props);
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
	Event event = t.getEvent();
	t.leaving().forEach(l -> listener.leaving(state, l, event));
	listener.following(state, t);
	t.entering().forEach(e -> listener.entering(state, e, event));

	current = t.getDestination();
	reachCurrentState(event);
    }

    void start() {
	InstanceState state = getInstanceState();
	Event event = Started.INSTANCE;
	if (parentId == null) {
	    current.states().forEach(s -> listener.entering(state, s, event));
	} else
	    listener.entering(state, current, event);

	reachCurrentState(event);
    }

    private void reachCurrentState(Event event) {
	StateType type = automat.type(current);
	listener.reaching(getInstanceState(), current, type, event);
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
