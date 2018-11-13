package fr.keyser.n.fsm.automat;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;
import fr.keyser.n.fsm.Transition;

public class Automat {

    private final State initial;

    private final Map<State, StateNode> stateNodes;

    Automat(State initial, Map<State, StateNode> stateNodes) {
	this.initial = initial;
	this.stateNodes = stateNodes;
    }

    Stream<Transition> findTransitions(State current, Event event) {
	return opt(current)
	        .map(node -> node.findTransitions(event))
	        .orElse(Stream.empty());
    }

    State getInitial() {
	return initial;
    }

    @Override
    public String toString() {
	return stateNodes.values().stream().map(s -> initial == s.getState() ? "* " + s : s.toString()).collect(Collectors.joining("\n"));
    }

    StateType type(State state) {
	return opt(state)
	        .map(StateNode::getType)
	        .orElse(null);
    }

    boolean isTerminal(State state) {
	return opt(state)
	        .map(StateNode::isTerminal)
	        .orElse(false);
    }

    boolean isOrthogonal(State state) {
	return opt(state)
	        .map(StateNode::isOrthogonal)
	        .orElse(false);
    }

    Stream<State> orthogonalChilds(State state) {
	return opt(state)
	        .map(StateNode::orthogonalChilds)
	        .orElse(Stream.empty());
    }

    boolean isJoining(State state) {
	return opt(state)
	        .map(StateNode::isJoining)
	        .orElse(false);
    }

    private Optional<StateNode> opt(State state) {
	return Optional.ofNullable(stateNodes.get(state));
    }
}
