package fr.keyser.n.fsm.automat;

import java.util.Optional;
import java.util.stream.Stream;

import fr.keyser.n.fsm.Event;
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
	return listener.guard(id, transition);
    }

    void receive(Event event) {
	Stream<Transition> transitions = automat.findTransitions(current, event);

	Optional<Transition> transition = transitions.filter(this::guard).findFirst();
	if (transition.isPresent()) {
	    Transition t = transition.get();

	    follow(t);
	}
    }

    private void follow(Transition t) {
	t.leaving().forEach(l -> listener.leaving(id, l, t.getEvent()));
	listener.following(id, t);
	t.entering().forEach(e -> listener.entering(id, e, t.getEvent()));

	current = t.getDestination();
	reachCurrentState();
    }

    void start() {
	if (parentId == null) {
	    current.states().forEach(s -> listener.entering(id, s, Started.INSTANCE));
	} else
	    listener.entering(id, current, Started.INSTANCE);

	reachCurrentState();
    }

    private void reachCurrentState() {
	StateType type = automat.type(current);
	listener.reaching(id, current, type);
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
