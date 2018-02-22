package fr.keyser.fsm2;

import java.util.concurrent.CompletionStage;

public interface DelayedEventConsumer<S, E> {

    default CompletionStage<RoutingStatus<S, E>> push(E event) {
	return push(Event.create(event));
    }

    default CompletionStage<RoutingStatus<S, E>> push(Event.Builder<E> event) {
	return push(event.build());
    }

    CompletionStage<RoutingStatus<S, E>> push(Event<E> event);

}