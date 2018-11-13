package fr.keyser.n.fsm.listener;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceId;
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
    public void entering(InstanceId id, State entered, Event event) {
	if (listener != null)
	    listener.entering(id, entered, event);
    }

    @Override
    public void following(InstanceId id, Transition transition) {
	if (listener != null)
	    listener.following(id, transition);
    }

    @Override
    public boolean guard(InstanceId id, Transition transition) {
	if (listener != null)
	    return listener.guard(id, transition);
	else
	    return true;
    }

    @Override
    public void leaving(InstanceId id, State leaved, Event event) {
	if (listener != null)
	    listener.leaving(id, leaved, event);
    }

    @Override
    public void reaching(InstanceId id, State reached, StateType type) {
	if (listener != null)
	    listener.reaching(id, reached, type);
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
    public boolean guard(InstanceId id, Event event) {
	if (listener != null)
	    return listener.guard(id, event);
	else
	    return true;
    }

}
