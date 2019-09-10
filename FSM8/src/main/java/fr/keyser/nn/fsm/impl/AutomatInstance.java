package fr.keyser.nn.fsm.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.State;

class AutomatInstance<T> implements Instance<T> {

    static <T> AutomatInstance<T> initial(Automats<T> container, InstanceId instanceId, State state, T payload) {
	return new AutomatInstance<T>(container, instanceId, state, payload, null, null);
    }

    private final Automats<T> container;

    private final InstanceId instanceId;

    private final InstanceId parentId;

    private final T payload;

    private final State region;

    private final State state;

    private AutomatInstance(Automats<T> container, InstanceId instanceId, State state, T payload, InstanceId parentId, State region) {
	this.container = container;
	this.instanceId = instanceId;
	this.state = state;
	this.payload = payload;
	this.parentId = parentId;
	this.region = region;
    }

    AutomatInstance<T> child(InstanceId instanceId, State state, T payload) {
	return new AutomatInstance<>(container, instanceId, state, payload, this.instanceId, this.state);
    }

    @Override
    public List<Instance<T>> childsInstances() {
	return container.childsOf(this.getInstanceId());
    }

    @Override
    public <X> Optional<X> opt(Function<T, X> getter) {
	return Optional.ofNullable(payload).map(getter);
    }

    @Override
    public InstanceId getInstanceId() {
	return instanceId;
    }

    @Override
    public Instance<T> getParent() {
	if (parentId == null)
	    return null;

	return container.getInstance(parentId);
    }

    InstanceId getParentId() {
	return parentId;
    }

    @Override
    public State getRegion() {
	return region;
    }

    @Override
    public State getState() {
	return state;
    }

    @Override
    public void startChild(State childState, EventMsg start, int index) {
	container.startChild(this, childState, start, index);
    }

    AutomatInstance<T> state(State state) {
	return new AutomatInstance<T>(container, instanceId, state, payload, parentId, region);
    }

    @Override
    public void submit(int index, EventMsg event) {
	container.submit(index, event);

    }

    @Override
    public String toString() {
	return "AutomatInstance [instanceId=" + instanceId + ", state=" + state + ", payload=" + payload + ", parentId=" + parentId
	        + ", region=" + region + "]";
    }

    @Override
    public AutomatInstance<T> update(UnaryOperator<T> operator) {
	return new AutomatInstance<T>(container, instanceId, state, operator.apply(payload), parentId, region);
    }
}
