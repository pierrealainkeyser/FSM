package fr.keyser.nn.fsm.impl;

import java.util.stream.Stream;

import fr.keyser.n.fsm.State;

public abstract class SingleDestinationTransitionSource implements TransitionSource {

    protected final State destination;

    public SingleDestinationTransitionSource(State destination) {
	this.destination = destination;
    }

    @Override
    public Stream<Transition> transition(State source, EventMsg event) {
	return Stream.of(new Transition(source, event, destination));
    }

}