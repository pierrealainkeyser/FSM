package fr.keyser.n.fsm.automat;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.keyser.n.fsm.EventReceiver;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.LazyEventReceiver;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.listener.choice.ChoiceAutomatListener;
import fr.keyser.n.fsm.listener.choice.Choices;
import fr.keyser.n.fsm.listener.frontier.EntryAutomatListener;
import fr.keyser.n.fsm.listener.frontier.EntryListener;
import fr.keyser.n.fsm.listener.logging.LoggingAutomatListener;
import fr.keyser.n.fsm.listener.timeout.LazyTimeoutScheduler;
import fr.keyser.n.fsm.listener.timeout.TimeOutAutomatListener;
import fr.keyser.n.fsm.listener.timeout.TimeoutScheduler;

public class AutomatContainerBuilder {

    public class StateConfigurer {
	private final State state;

	private StateConfigurer(State state) {
	    this.state = state;
	}

	public Choices with(String index, Supplier<Boolean> supplier) {
	    return choiceListener.choices(state).with(index, supplier);
	}

	public Choices choices() {
	    return choiceListener.choices(state);
	}

	public StateConfigurer timeout(Supplier<Duration> supplier) {
	    timerListener.timeout(state, supplier);
	    return this;
	}

	public StateConfigurer entry(EntryListener el) {
	    entries.entry(state, el);
	    return this;
	}

	public StateConfigurer exit(EntryListener el) {
	    entries.exit(state, el);
	    return this;
	}

	public StateConfigurer entry(Runnable run) {
	    entries.entry(state, run);
	    return this;
	}

	public StateConfigurer exit(Runnable run) {
	    entries.exit(state, run);
	    return this;
	}

	public StateConfigurer entry(Consumer<InstanceState> run) {
	    entries.entry(state, run);
	    return this;
	}

	public StateConfigurer exit(Consumer<InstanceState> run) {
	    entries.exit(state, run);
	    return this;
	}

	public AutomatContainerBuilder and() {
	    return AutomatContainerBuilder.this;
	}
    }

    private final Automat automat;

    private final ChoiceAutomatListener choiceListener;

    private final EntryAutomatListener entries = new EntryAutomatListener();

    private final LazyEventReceiver receiver = new LazyEventReceiver();

    private final LazyTimeoutScheduler timeOutScheduler = new LazyTimeoutScheduler();

    private final TimeOutAutomatListener timerListener;

    public AutomatContainerBuilder(Automat automat) {
	this.automat = automat;

	timerListener = new TimeOutAutomatListener(entries, timeOutScheduler);
	choiceListener = new ChoiceAutomatListener(receiver, timerListener);
    }

    public AutomatContainer build() {
	AutomatContainer container = new AutomatContainer(automat, new LoggingAutomatListener(choiceListener));
	receiver.setEventReceiver(container);
	return container;
    }

    public StateConfigurer state(State state) {
	if (!automat.containsState(state))
	    throw new IllegalArgumentException("No such state " + state);

	return new StateConfigurer(state);
    }

    public AutomatContainerBuilder timeoutScheduler(TimeoutScheduler timeoutScheduler) {
	timeOutScheduler.setTimeoutScheduler(timeoutScheduler);
	return this;
    }

    public EventReceiver getReceiver() {
	return receiver;
    }
}
