package fr.keyser.fsm;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

public class StateMachine<S, E> implements DelayedEventConsumer<S, E> {

    private static final Logger logger = LoggerFactory.getLogger(StateMachine.class);

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

	    if (logger.isDebugEnabled())
		logger.debug("{} -> before routing : {}", ObjectUtils.getIdentityHexString(StateMachine.this), event.getValue());

	    NodeState<S, E> working = root.lookup(current);
	    RoutingStatus<S, E> status = null;
	    if (working == null)
		status = RoutingStatus.noStateFound(current);
	    else {
		status = working.onEvent(event);
		processStatus(status);
	    }

	    if (logger.isDebugEnabled())
		logger.debug("{} -> after routing : {}", ObjectUtils.getIdentityHexString(StateMachine.this), status);

	    return status;

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

    public List<State<S>> getChildrens() {
	return root.getChildrens();
    }
}
