package fr.keyser.n.fsm;

import java.util.stream.Stream;

public final class Transition {

    private final State destination;

    private final Event event;

    private final State source;

    public Transition(State source, Event event, State destination) {
	this.source = source;
	this.event = event;
	this.destination = destination;
    }

    public Stream<State> leaving() {
	return source.diff(destination, true);
    }

    public Stream<State> entering() {
	return destination.diff(source, false);
    }

    public State getDestination() {
	return destination;
    }

    public Event getEvent() {
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
