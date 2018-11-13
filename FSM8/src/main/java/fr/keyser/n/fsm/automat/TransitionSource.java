package fr.keyser.n.fsm.automat;

import java.util.stream.Stream;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.Transition;

public interface TransitionSource {

    Stream<Transition> transition(State source, Event event);

}