package fr.keyser.n.fsm.automat;

import java.util.function.Predicate;

import fr.keyser.n.fsm.Event;

public class Joined extends Event {

    public final static Joined INSTANCE = new Joined();

    public final static JoinedPredicate PREDICATE = new JoinedPredicate();

    private Joined() {
	super("<joined>");
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
