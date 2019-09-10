package fr.keyser.nn.fsm.impl;

@FunctionalInterface
public interface MergeFunction<T> {

    public T merge(T current, Object payload);

}
