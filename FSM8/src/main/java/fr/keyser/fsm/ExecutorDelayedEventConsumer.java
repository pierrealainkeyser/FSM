package fr.keyser.fsm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

public class ExecutorDelayedEventConsumer<S, E> implements DelayedEventConsumer<S, E> {

    private final DelayedEventConsumer<S, E> delegated;

    private final Executor executor;

    public ExecutorDelayedEventConsumer(Executor executor, DelayedEventConsumer<S, E> delegated) {
	this.executor = executor;
	this.delegated = delegated;
    }

    @Override
    public CompletionStage<RoutingStatus<S, E>> push(Event<E> event) {
	CompletableFuture<RoutingStatus<S, E>> cf = new CompletableFuture<>();
	executor.execute(() -> {
	    CompletionStage<RoutingStatus<S, E>> pushed = delegated.push(event);
	    pushed.whenComplete((r, e) -> {
		if (e != null)
		    cf.completeExceptionally(e);
		else
		    cf.complete(r);
	    });
	});
	return cf;
    }

}
