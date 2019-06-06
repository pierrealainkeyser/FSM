package fr.keyser.n.fsm.automat;

import java.time.Duration;
import java.util.Map;
import java.util.function.Predicate;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.State;

public class TimeOut extends Event {

    public static final String KEY = "<timeout>";
    private static final String DURATION = "duration";
    private static final String TIMER = "timer";
    private static final String STATE = "state";

    public static class TimedOutPredicate implements Predicate<Event> {
	private TimedOutPredicate() {
	}

	@Override
	public boolean test(Event t) {
	    return t instanceof TimeOut;
	}

	@Override
	public String toString() {
	    return "!";
	}
    }

    public final static TimedOutPredicate PREDICATE = new TimedOutPredicate();

    public static TimeOut timeout(InstanceId id, State state, long timer, Duration duration) {
	return Event.event(KEY)
	        .id(id)
	        .put(STATE, state)
	        .put(TIMER, timer)
	        .put(DURATION, duration)
	        .build(TimeOut::new);
    }

    private TimeOut(String key, Map<String, Object> args) {
	super(key, args);
    }

    public Duration getDuration() {
	return (Duration) get(DURATION);
    }

    public State getState() {
	return (State) get(STATE);
    }

    public long getTimer() {
	return (Long) get(TIMER);
    }
}
