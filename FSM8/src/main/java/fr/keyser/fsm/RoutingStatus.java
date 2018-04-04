package fr.keyser.fsm;

public class RoutingStatus<S, E> {

    public enum Result {
	OK, INVALID_EVENT, NO_ROUTE_FOUND, NO_STATE_FOUND
    }

    public static <S, E> RoutingStatus<S, E> done(State<S> from, State<S> to, E event) {
	return new RoutingStatus<>(from, to, event, Result.OK);
    }

    public static <S, E> RoutingStatus<S, E> invalidEvent(State<S> from, E event) {
	return new RoutingStatus<>(from, null, event, Result.INVALID_EVENT);
    }

    public static <S, E> RoutingStatus<S, E> noRouteFound(State<S> from, E event) {
	return new RoutingStatus<>(from, null, event, Result.NO_ROUTE_FOUND);
    }

    public static <S, E> RoutingStatus<S, E> noStateFound(State<S> from) {
	return new RoutingStatus<>(from, null, null, Result.NO_STATE_FOUND);
    }

    private final E event;

    private final State<S> from;

    private final Result result;

    private final State<S> to;

    private RoutingStatus(State<S> from, State<S> to, E event, Result result) {
	this.from = from;
	this.to = to;
	this.event = event;
	this.result = result;
    }

    public boolean isOK() {
	return Result.OK == result;
    }

    public boolean isInvalidEvent() {
	return Result.INVALID_EVENT == result;
    }

    public boolean isNoRouteFound() {
	return Result.NO_ROUTE_FOUND == result;
    }

    public boolean isNoStateFound() {
	return Result.NO_STATE_FOUND == result;
    }

    public RoutingStatus<S, E> mergeFinalState(State<S> finalState) {
	return new RoutingStatus<>(from, finalState, event, result);
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

    @Override
    public String toString() {
	return "from=" + from + ", event=" + event + ", " + result + ", to=" + to;
    }
}
