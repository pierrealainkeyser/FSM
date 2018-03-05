package fr.keyser.fsm;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fr.keyser.fsm.DelayedEventConsumer;
import fr.keyser.fsm.Event;
import fr.keyser.fsm.Event.Builder;

class TimerLogic {

    private static final String NUMLOCK = "numlock";

    public static final String UNIT = "unit";

    public static final String DELAY = "delay";

    public static final String EVENT = "event";

    private long delay;

    private Runnable event;

    private TimeUnit unit;

    private final DelayedEventConsumer<?, String> consumer;

    private Future<?> current;

    private int numlock;

    private final ScheduledExecutorService scheduler;

    public TimerLogic(ScheduledExecutorService scheduler, DelayedEventConsumer<?, String> consumer) {
	this.scheduler = scheduler;
	this.consumer = consumer;
    }

    public void armTimer() {
	++numlock;
	Builder<String> event = Event.create(TimerMachine.TIMEOUT).put(NUMLOCK, numlock);

	Runnable command = () -> consumer.push(event);
	current = scheduler.schedule(command, delay, unit);

    }

    public boolean checkTimeout(Event<String> event) {
	Object object = event.get(NUMLOCK);
	if (object instanceof Integer)
	    return ((Integer) object).intValue() == numlock;
	else
	    return false;
    }

    public void disarmTimer() {
	if (current != null) {
	    current.cancel(false);
	    current = null;
	}
    }

    public void fireTimeout() {
	event.run();
    }

    public void setupTimer(Event<String> event) {
	this.event = (Runnable) event.get(EVENT);
	delay = (long) event.getOrDefault(DELAY, 20l);
	unit = (TimeUnit) event.getOrDefault(UNIT, TimeUnit.SECONDS);
    }

    public void unsetupTimer() {
	event = null;
	delay = -1l;
	unit = null;
    }

}
