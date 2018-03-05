package fr.keyser.fsm;

public class RoutingStatus<S, E> {

    public static <S, E> RoutingStatus<S, E> done(State<S> from, State<S> to, E event) {
	return new RoutingStatus<>(from, to, event);
    }

    public static <S, E> RoutingStatus<S, E> invalidEvent(State<S> from, E event) {
	return new RoutingStatus<>(from, null, event);
    }

    public static <S, E> RoutingStatus<S, E> noRouteFound(State<S> from, E event) {
	return new RoutingStatus<>(from, null, event);
    }

    public static <S, E> RoutingStatus<S, E> noStateFound(State<S> from) {
	return new RoutingStatus<>(from, null, null);
    }

    private final E event;

    private final State<S> from;

    private final State<S> to;

    private RoutingStatus(State<S> from, State<S> to, E event) {
	this.from = from;
	this.to = to;
	this.event = event;
    }

    public RoutingStatus<S, E> mergeFinalState(State<S> finalState) {
	return new RoutingStatus<>(from, finalState, event);
    }

    public E getEvent() {
	return event;
    }

    public State<S> getFrom() {
	return from;
    }

    public State<S> getTo() {
	return to;
    }
}
