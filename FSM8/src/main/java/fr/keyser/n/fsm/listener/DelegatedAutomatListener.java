package fr.keyser.n.fsm.listener;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;
import fr.keyser.n.fsm.Transition;

public class DelegatedAutomatListener implements AutomatListener {
    private final AutomatListener listener;

    protected DelegatedAutomatListener() {
	this(null);
    }

    protected DelegatedAutomatListener(AutomatListener listener) {
	this.listener = listener;
    }

    @Override
    public void entering(InstanceState id, State entered, Event event) {
	if (listener != null)
	    listener.entering(id, entered, event);
    }

    @Override
    public void following(InstanceState instance, Transition transition) {
	if (listener != null)
	    listener.following(instance, transition);
    }

    @Override
    public boolean guard(InstanceState instance, Transition transition) {
	if (listener != null)
	    return listener.guard(instance, transition);
	else
	    return true;
    }

    @Override
    public void leaving(InstanceState instance, State leaved, Event event) {
	if (listener != null)
	    listener.leaving(instance, leaved, event);
    }

    @Override
    public void reaching(InstanceState instance, State reached, StateType type, Event event) {
	if (listener != null)
	    listener.reaching(instance, reached, type, event);
    }

    @Override
    public void starting(InstanceState instance) {
	if (listener != null)
	    listener.starting(instance);
    }

    @Override
    public void terminating(InstanceState instance) {
	if (listener != null)
	    listener.terminating(instance);
    }

    @Override
    public boolean guard(InstanceState id, Event event) {
	if (listener != null)
	    return listener.guard(id, event);
	else
	    return true;
    }

}
