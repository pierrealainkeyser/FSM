package fr.keyser.n.fsm.automat;

import java.util.function.Supplier;

public class KeyedPreTransitionBuilder implements Supplier<SingleDestinationTransition> {

    private final StateBuilder destination;
    private final String key;

    public KeyedPreTransitionBuilder(String key, StateBuilder destination) {
	this.key = key;
	this.destination = destination;
    }

    @Override
    public SingleDestinationTransition get() {
	return new SingleDestinationTransition(key, destination.first().getState());
    }
}