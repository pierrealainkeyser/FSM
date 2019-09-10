package fr.keyser.nn.fsm.impl;

import fr.keyser.n.fsm.State;

public class DelegatedAutomatsListener<T> implements AutomatsListener<T> {

    private final AutomatsListener<T> listener;

    public DelegatedAutomatsListener(AutomatsListener<T> listener) {
	this.listener = listener;
    }

    @Override
    public Instance<T> entering(Instance<T> instance, State entered, EventMsg event) {
	return listener.entering(instance, entered, event);
    }

    @Override
    public boolean guard(Instance<T> instance, EventMsg event) {
	return listener.guard(instance, event);
    }

    @Override
    public boolean guard(Instance<T> instance, Transition transition) {
	return listener.guard(instance, transition);
    }

    @Override
    public Instance<T> leaving(Instance<T> instance, State leaved, EventMsg event) {
	return listener.leaving(instance, leaved, event);
    }

    @Override
    public T start(Start start) {
	return listener.start(start);
    }

    @Override
    public T start(Instance<T> parent, EventMsg event, int index) {
	return listener.start(parent, event, index);
    }

    @Override
    public Instance<T> terminating(Instance<T> instance) {
	return listener.terminating(instance);
    }

}
