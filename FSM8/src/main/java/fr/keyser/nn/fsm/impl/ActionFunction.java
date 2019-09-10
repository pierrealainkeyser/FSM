package fr.keyser.nn.fsm.impl;

@FunctionalInterface
public interface ActionFunction<T> {

    public Instance<T> action(Instance<T> current, EventMsg event);

}
