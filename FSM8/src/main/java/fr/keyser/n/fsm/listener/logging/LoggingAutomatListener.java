package fr.keyser.n.fsm.listener.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;
import fr.keyser.n.fsm.Transition;
import fr.keyser.n.fsm.listener.AutomatListener;
import fr.keyser.n.fsm.listener.DelegatedAutomatListener;

public class LoggingAutomatListener extends DelegatedAutomatListener {

    private final static Logger logger = LoggerFactory.getLogger(LoggingAutomatListener.class);

    public LoggingAutomatListener(AutomatListener listener) {
	super(listener);
    }

    @Override
    public void entering(InstanceId id, State entered, Event event) {
	logger.debug("entering {} {} @{}", id, entered, event);
	super.entering(id, entered, event);
    }

    @Override
    public void following(InstanceId id, Transition transition) {
	logger.debug("following {} {} ", id, transition);
	super.following(id, transition);
    }

    @Override
    public boolean guard(InstanceId id, Transition transition) {
	boolean guarded = super.guard(id, transition);
	if (!guarded) {
	    logger.debug("guard reject transition {} {} ", id, transition);
	}
	return guarded;
    }

    @Override
    public void leaving(InstanceId id, State leaved, Event event) {
	logger.debug("leaving {} {} @{}", id, leaved, event);
	super.leaving(id, leaved, event);
    }

    @Override
    public void reaching(InstanceId id, State reached, StateType type) {
	logger.debug("reaching {} {}({})", id, reached, type);
	super.reaching(id, reached, type);
    }

    @Override
    public void starting(InstanceState instance) {
	logger.debug("starting {}", instance);
	super.starting(instance);
    }

    @Override
    public void terminating(InstanceState instance) {
	logger.debug("terminating {}", instance);
	super.terminating(instance);
    }

    @Override
    public boolean guard(InstanceId id, Event event) {
	boolean guarded = super.guard(id, event);
	if (!guarded) {
	    logger.debug("guard reject event {} {} ", id, event);
	}
	return guarded;
    }

}
