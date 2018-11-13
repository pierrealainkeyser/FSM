package fr.keyser.n.fsm.automat;

import java.util.function.Predicate;
import java.util.stream.Stream;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.Transition;

public class SingleDestinationTransition implements TransitionSource {

    private static class EqualKey implements Predicate<Event> {
	private final String key;

	protected EqualKey(String key) {
	    this.key = key;
	}

	@Override
	public boolean test(Event t) {
	    return key.equals(t.getKey());
	}

	@Override
	public String toString() {
	    return "@" + key;
	}
    }

    private final State destination;

    private final Predicate<Event> eventMatcher;

    public SingleDestinationTransition(Predicate<Event> expectedKey, State destination) {
	this.eventMatcher = expectedKey;
	this.destination = destination;
    }

    public SingleDestinationTransition(String expectedKey, State destination) {
	this(new EqualKey(expectedKey), destination);
    }

    @Override
    public String toString() {
	return eventMatcher + "->" + destination;
    }


    @Override
    public Stream<Transition> transition(State source, Event event) {
	if (eventMatcher.test(event)) {
	    return Stream.of(new Transition(source, event, destination));
	} else
	    return Stream.empty();
    }
}