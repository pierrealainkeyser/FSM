package fr.keyser.nn.fsm.impl;

import java.util.stream.Stream;

import fr.keyser.n.fsm.State;

public final class Transition {

    private final State destination;

    private final EventMsg event;

    private final State source;

    private final boolean reentrant;

    public Transition(State source, EventMsg event, State destination) {
	this.source = source;
	this.event = event;
	this.destination = destination;

	this.reentrant = source.equals(destination);
    }

    public Stream<State> leaving() {
	if (reentrant)
	    return Stream.of(source);

	return source.diff(destination, true);
    }

    public Stream<State> entering() {
	if (reentrant)
	    return Stream.of(destination);

	return destination.diff(source, false);
    }

    public State getDestination() {
	return destination;
    }

    public EventMsg getEvent() {
	return event;
    }

    public State getSource() {
	return source;
    }

    @Override
    public String toString() {
	return source + " @" + event + " -> " + destination;
    }
}
