package fr.keyser.fsm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

public class StateMachine<S, E> implements DelayedEventConsumer<S, E> {

    private State<S> current;

    private final Executor executor;

    private final NodeState<S, E> root;

    StateMachine(Executor sequential, NodeState<S, E> root) {
	this.executor = sequential;
	this.root = root;
	this.current = root.firstChildOrSelf().getState();
    }

    @Override
    public CompletionStage<RoutingStatus<S, E>> push(Event<E> event) {
	CompletionStage<RoutingStatus<S, E>> cs = CompletableFuture.supplyAsync(() -> {
	    NodeState<S, E> working = root.lookup(current);
	    if (working == null)
		return RoutingStatus.noStateFound(current);
	    else {
		RoutingStatus<S, E> status = working.onEvent(event);
		processStatus(status);
		return status;
	    }

	}, executor);
	return cs.thenApply(this::merge);
    }

    private CompletionStage<RoutingStatus<S, E>> enterState0(State<S> current) {
	return CompletableFuture.supplyAsync(() -> {
	    NodeState<S, E> working = root.lookup(current);
	    if (working == null)
		return RoutingStatus.noStateFound(current);
	    else
		return working.fireEntry();

	}, executor);
    }

    public CompletionStage<RoutingStatus<S, E>> enterState(State<S> current) {
	return enterState0(current).thenApply(i -> setStateAndMerge(current, i));
    }

    private RoutingStatus<S, E> setStateAndMerge(State<S> current, RoutingStatus<S, E> input) {
	this.current = current;
	return input.mergeFinalState(this.current);
    }

    public CompletionStage<RoutingStatus<S, E>> enterInitialState() {
	return enterState0(current);
    }

    private RoutingStatus<S, E> merge(RoutingStatus<S, E> input) {
	return input.mergeFinalState(current);
    }

    private void processStatus(RoutingStatus<S, E> status) {

	State<S> to = status.getTo();
	if (to != null) {
	    current = to;
	}
    }

    public State<S> getCurrent() {
	return current;
    }
}
