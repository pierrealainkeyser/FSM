package fr.keyser.n.fsm.automat;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.Transition;
import fr.keyser.n.fsm.listener.choice.Choice;

public class ChoiceTransitionSource implements TransitionSource {

    private final Map<String, State> destinations;

    ChoiceTransitionSource(Map<String, State> destinations) {
	this.destinations = new LinkedHashMap<>(destinations);
    }

    @Override
    public Stream<Transition> transition(State source, Event event) {
	if (event instanceof Choice) {
	    Choice c = (Choice) event;
	    Stream.Builder<Transition> builder = Stream.builder();
	    Iterator<Entry<String, State>> it = destinations.entrySet().iterator();
	    while (it.hasNext()) {
		Entry<String, State> e = it.next();
		builder.add(new Transition(source, c.build(e.getKey(), !it.hasNext()), e.getValue()));
	    }
	    return builder.build();
	} else
	    return Stream.empty();
    }

    @Override
    public String toString() {
	return "?" + destinations.values().stream().map(Object::toString).collect(Collectors.joining("|"));
    }

}
