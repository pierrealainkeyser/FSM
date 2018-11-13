package fr.keyser.n.fsm.automat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;
import fr.keyser.n.fsm.Transition;

class StateNode {

    private final StateNode parent;

    private final State state;

    private final List<TransitionSource> transitions;

    private final StateType type;

    private final List<StateNode> childs = new ArrayList<>();

    StateNode(StateType type, StateNode parent, State state, List<TransitionSource> transitions) {
	this.parent = parent;
	this.state = state;
	this.type = type;
	this.transitions = transitions;

	if (this.parent != null) {
	    this.parent.childs.add(this);
	}
    }

    StateNode first() {
	if (StateType.COMPOSITE == type && !childs.isEmpty())
	    return childs.get(0).first();
	else
	    return this;
    }

    Stream<Transition> findTransitions(Event event) {
	Stream<Transition> current = transitions.stream().flatMap(p -> p.transition(state, event));
	if (parent != null) {
	    return Stream.concat(current, parent.findTransitions(event));
	}
	return current;
    }

    boolean isTerminal() {
	return type == StateType.TERMINAL;
    }

    Stream<State> orthogonalChilds() {
	return childs.stream().filter(s -> !s.isJoining()).map(s -> s.first().state);
    }

    boolean isOrthogonal() {
	return type == StateType.ORTHOGONAL;
    }

    boolean isJoining() {
	return type == StateType.JOINING;
    }

    State getState() {
	return state;
    }

    @Override
    public String toString() {
	String base = state + "(" + type + ")";
	if (isTerminal() || transitions.isEmpty()) {
	    return base;
	} else {
	    return base + " " + transitions.stream().map(TransitionSource::toString).collect(Collectors.joining(" "));
	}
    }

    StateType getType() {
	return type;
    }
}