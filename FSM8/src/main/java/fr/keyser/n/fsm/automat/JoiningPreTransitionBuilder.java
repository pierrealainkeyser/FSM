package fr.keyser.n.fsm.automat;

import java.util.function.Supplier;

public class JoiningPreTransitionBuilder implements Supplier<SingleDestinationTransition> {

    private final StateBuilder destination;

    public JoiningPreTransitionBuilder(StateBuilder destination) {
	this.destination = destination;
    }

    @Override
    public SingleDestinationTransition get() {
	return new SingleDestinationTransition(Joined.PREDICATE, destination.first().getState());
    }
}