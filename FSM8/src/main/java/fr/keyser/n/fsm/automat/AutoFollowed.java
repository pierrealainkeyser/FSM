package fr.keyser.n.fsm.automat;

import java.util.Map;
import java.util.function.Predicate;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceState;

public class AutoFollowed extends Event {

    public final static AutoPredicate PREDICATE = new AutoPredicate();

    public static AutoFollowed auto(InstanceState state) {
	return Event.event("<auto>")
	        .id(state)
	        .build(AutoFollowed::new);
    }

    protected AutoFollowed(String key, Map<String, Object> args) {
	super(key, args);
    }

    public static class AutoPredicate implements Predicate<Event> {
	private AutoPredicate() {
	}

	@Override
	public boolean test(Event t) {
	    return t instanceof AutoFollowed;
	}

	@Override
	public String toString() {
	    return "";
	}
    }

}
