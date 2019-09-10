package fr.keyser.nn.fsm.builder;

import fr.keyser.nn.fsm.impl.TransitionGuard;

public interface Guard<T> {
    
    public Guard<T> guard(TransitionGuard<T> guard);

    public Node<T> and();

}
