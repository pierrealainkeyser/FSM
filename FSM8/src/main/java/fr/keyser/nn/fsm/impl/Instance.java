package fr.keyser.nn.fsm.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.State;

public interface Instance<T> extends EventEndpoint {

    public InstanceId getInstanceId();

    public State getState();

    public <X> Optional<X> opt(Function<T, X> getter);

    public default <X> boolean test(Function<T, X> getter, Predicate<X> predicate) {
	return opt(getter).map(t -> predicate.test(t)).orElse(false);
    }

    public default <X> X get(Function<T, X> getter) {
	return opt(getter).get();
    }

    public Instance<T> update(UnaryOperator<T> operator);

    public default void unicast(int priority, String key) {
	submit(priority, EventMsg.unicast(key, getInstanceId()));
    }

    public default void unicast(String key) {
	unicast(key, null);
    }

    public default void unicast(String key, Object payload) {
	submit(EventMsg.unicast(key, getInstanceId(), payload));
    }

    public default void broadcast(String key, Object payload) {
	submit(EventMsg.broadcast(key, payload));
    }

    public default void broadcast(String key) {
	broadcast(key, null);
    }

    // ---- region management managements --------
    public Instance<T> getParent();

    public default void sendMerge(Object payload) {
	sendMerge(0, payload);
    }

    public default void sendMerge(int priority, Object payload) {
	Instance<T> parent = getParent();
	parent.submit(Merge.merge(parent.getInstanceId(), payload, getRegion()));
    }

    public State getRegion();

    public void startChild(State childState, EventMsg start, int index);

    public List<Instance<T>> childsInstances();
}
