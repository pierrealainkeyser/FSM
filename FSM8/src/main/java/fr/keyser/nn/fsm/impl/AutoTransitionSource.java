package fr.keyser.nn.fsm.impl;

import fr.keyser.n.fsm.State;

public class AutoTransitionSource extends SingleDestinationTransitionSource {

    public AutoTransitionSource(State destination) {
	super(destination);
    }

    @Override
    public String toString() {
	return Auto.KEY + " -> " + destination;
    }

    @Override
    public boolean accept(EventMsg event) {
	return event instanceof Auto;
    }

}
