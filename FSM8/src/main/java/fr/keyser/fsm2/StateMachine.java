package fr.keyser.fsm2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import fr.keyser.fsm.SequentialExecutor;

public class StateMachine<S, E> implements DelayedEventConsumer<S, E> {

    private State<S> current;

    private final SequentialExecutor executor;

    private final NodeState<S, E> root;

    StateMachine(SequentialExecutor executor, NodeState<S, E> root, DelegatedDelayedEventConsumer<S, E> delegated) {
	this.executor = executor;
	this.root = root;
	delegated.setDelegated(this);
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

    public CompletionStage<RoutingStatus<S, E>> enterInitialState() {
	return CompletableFuture.supplyAsync(() -> {
	    NodeState<S, E> working = root.lookup(current);
	    if (working == null)
		return RoutingStatus.noStateFound(current);
	    else {
		return working.fireEntry();
	    }

	}, executor);
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
