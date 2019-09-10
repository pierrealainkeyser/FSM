package fr.keyser.nn.fsm.impl;

import java.util.stream.Stream;

import fr.keyser.n.fsm.State;

public class MergingTransitionSource implements TransitionSource {

    public final static MergingTransitionSource INSTANCE = new MergingTransitionSource();

    private MergingTransitionSource() {
	//
    }

    @Override
    public String toString() {
	return "<merging>";
    }

    @Override
    public boolean accept(EventMsg event) {
	return event instanceof Merge;
    }

    @Override
    public Stream<Transition> transition(State source, EventMsg event) {
	return Stream.of(new Transition(source, event, source));
    }

}
