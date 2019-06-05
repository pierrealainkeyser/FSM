package fr.keyser.n.fsm;

import java.util.stream.Stream;

import fr.keyser.n.fsm.listener.timeout.TimeOut;

public final class Transition {

    private final State destination;

    private final Event event;

    private final State source;

    public Transition(State source, Event event, State destination) {
	this.source = source;
	this.event = event;
	this.destination = destination;
    }

    public boolean isTimeout() {
	return event instanceof TimeOut;
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
