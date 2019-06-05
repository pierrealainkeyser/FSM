package fr.keyser.n.fsm.listener.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.keyser.n.fsm.Event;
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
    public void entering(InstanceState instance, State entered, Event event) {
	logger.debug("entering {} {} @{}", instance, entered, event);
	super.entering(instance, entered, event);
    }

    @Override
    public void following(InstanceState instance, Transition transition) {
	logger.debug("following {} {} : {}", instance, transition, instance.getProps());
	super.following(instance, transition);
    }

    @Override
    public boolean guard(InstanceState instance, Transition transition) {
	boolean guarded = super.guard(instance, transition);
	if (!guarded) {
	    logger.debug("guard reject transition {} {} ", instance, transition);
	}
	return guarded;
    }

    @Override
    public void leaving(InstanceState instance, State leaved, Event event) {
	logger.debug("leaving {} {} @{}", instance, leaved, event);
	super.leaving(instance, leaved, event);
    }

    @Override
    public void reaching(InstanceState instance, State reached, StateType type) {
	logger.debug("reaching {} {}({})", instance, reached, type);
	super.reaching(instance, reached, type);
    }

    @Override
    public void starting(InstanceState instance) {
	logger.debug("starting {} : {}", instance, instance.getProps());
	super.starting(instance);
    }

    @Override
    public void terminating(InstanceState instance) {
	logger.debug("terminating {}", instance);
	super.terminating(instance);
    }

    @Override
    public boolean guard(InstanceState id, Event event) {
	boolean guarded = super.guard(id, event);
	if (!guarded) {
	    logger.debug("guard reject event {} {} ", id, event);
	}
	return guarded;
    }

}
