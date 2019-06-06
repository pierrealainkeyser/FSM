package fr.keyser.n.fsm.automat;

import java.util.function.Supplier;

public class AutoPreTransitionBuilder implements Supplier<SingleDestinationTransition> {

    private final StateBuilder destination;

    public AutoPreTransitionBuilder(StateBuilder destination) {
	this.destination = destination;
    }

    @Override
    public SingleDestinationTransition get() {
	return new SingleDestinationTransition(AutoFollowed.PREDICATE, destination.first().getState());
    }
}