package fr.keyser.nn.fsm.impl;

import fr.keyser.n.fsm.State;

public class JoinTransitionSource extends SingleDestinationTransitionSource {

    public JoinTransitionSource(State destination) {
	super(destination);
    }

    @Override
    public String toString() {
	return Joined.KEY + " -> " + destination;
    }

    @Override
    public boolean accept(EventMsg event) {
	return event instanceof Joined;
    }

}
