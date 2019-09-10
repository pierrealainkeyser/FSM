package fr.keyser.nn.fsm.impl;

@FunctionalInterface
public interface PayloadActionFunction<T> {

    public T action(T current, EventMsg event);

    public default ActionFunction<T> asAction() {
	return (i, event) -> i.update(p -> action(p, event));
    }

}
