package fr.keyser.fsm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class DelegatedDelayedEventConsumer<S, E> implements DelayedEventConsumer<S, E> {

    private DelayedEventConsumer<S, E> delegated;

    public void setDelegated(DelayedEventConsumer<S, E> delegated) {
	this.delegated = delegated;
    }

    @Override
    public CompletionStage<RoutingStatus<S, E>> push(Event<E> event) {
	if (delegated == null) {
	    CompletableFuture<RoutingStatus<S, E>> cf = new CompletableFuture<>();
	    cf.completeExceptionally(new IllegalStateException("No delegated consumer"));
	    return cf;
	}

	return delegated.push(event);
    }

}
