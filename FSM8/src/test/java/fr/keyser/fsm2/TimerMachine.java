package fr.keyser.fsm2;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import fr.keyser.fsm2.StateMachineBuilder.StateBuilder;

public class TimerMachine {

    private static final String ONLINE = "online";
    private static final String OFFLINE = "offline";
    private static final String DISARM = "disarm";
    private static final String ARM = "arm";
    public static final String TIMEOUT = "timeout";
    private final StateMachine<?, String> machine;

    private final TimerLogic logic;

    public TimerMachine(ScheduledExecutorService scheduler) {
	this(scheduler, StateMachineBuilder::new);
    }

    public TimerMachine(ScheduledExecutorService scheduler, Supplier<StateMachineBuilder<String, String>> factory) {

	StateMachineBuilder<String, String> builder = factory.get();
	logic = new TimerLogic(scheduler, builder.eventConsummer());

	StateBuilder<String, String> idle = builder.state("idle")
	        .onEntry(logic::unsetupTimer);
	StateBuilder<String, String> idleOff = idle.sub("off");
	StateBuilder<String, String> idleOn = idle.sub("on");

	StateBuilder<String, String> armed = builder.state("armed");
	StateBuilder<String, String> armedOff = armed.sub("off");
	StateBuilder<String, String> armedOn = armed.sub("on")
	        .onEntry(logic::armTimer)
	        .onExit(logic::disarmTimer);

	idleOn.transition(ARM, armedOn).onTransition(logic::setupTimer);
	armedOn.transition(DISARM, idleOn);
	armedOn.transition(TIMEOUT, idleOn).guard(logic::checkTimeout).onTransition(logic::fireTimeout);

	idleOff.transition(ARM, armedOff).onTransition(logic::setupTimer);
	armedOff.transition(DISARM, idleOff);
	armedOff.transition(TIMEOUT, idleOff).guard(logic::checkTimeout);

	idleOn.transition(OFFLINE, idleOff);
	armedOn.transition(OFFLINE, armedOff);

	idleOff.transition(ONLINE, idleOn);
	armedOff.transition(ONLINE, armedOn);

	machine = builder.build();
	machine.enterInitialState();
    }

    public void arm(Runnable command, long delay, TimeUnit unit) {
	machine.push(Event.create(ARM).put(TimerLogic.EVENT, command)
	        .put(TimerLogic.DELAY, delay)
	        .put(TimerLogic.UNIT, unit));
    }

    public void disarm() {
	machine.push(DISARM);
    }

    public void online() {
	machine.push(ONLINE);
    }

    public void offline() {
	machine.push(OFFLINE);
    }
}
