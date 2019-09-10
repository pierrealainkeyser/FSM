package fr.keyser.nn.fsm.impl;

import java.util.stream.Stream;

import fr.keyser.n.fsm.State;

public interface TransitionSource {

    boolean accept(EventMsg event);

    Stream<Transition> transition(State source, EventMsg event);

}