package fr.keyser.nn.fsm.impl;

@FunctionalInterface
public interface CreateChildFunction<T> {
    
    public T createChild(T parent, EventMsg event, int index);

}
