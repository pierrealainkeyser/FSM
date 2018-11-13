package fr.keyser.n.fsm.automat;

import java.util.function.Supplier;

import fr.keyser.n.fsm.listener.timeout.TimeOut;

public class TimedOutPreTransitionBuilder implements Supplier<SingleDestinationTransition> {

    private final StateBuilder destination;

    public TimedOutPreTransitionBuilder(StateBuilder destination) {
	this.destination = destination;
    }

    @Override
    public SingleDestinationTransition get() {
	return new SingleDestinationTransition(TimeOut.PREDICATE, destination.first().getState());
    }
}