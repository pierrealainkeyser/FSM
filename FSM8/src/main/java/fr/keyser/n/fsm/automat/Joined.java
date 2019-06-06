package fr.keyser.n.fsm.automat;

import java.util.Map;
import java.util.function.Predicate;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceId;

public class Joined extends Event {

    public final static JoinedPredicate PREDICATE = new JoinedPredicate();

    public final static Joined join(InstanceId id) {
	return Event.event("<joined>")
	        .id(id)
	        .build(Joined::new);
    }

    private Joined(String key, Map<String, Object> args) {
	super(key, args);

    }

    public static class JoinedPredicate implements Predicate<Event> {
	private JoinedPredicate() {
	}

	@Override
	public boolean test(Event t) {
	    return t instanceof Joined;
	}

	@Override
	public String toString() {
	    return "§";
	}
    }
}
