package fr.keyser.nn.fsm.builder;

public interface Node<T> extends Edge, Container<T>, ActionNode<T> {

    Guard<T> event(String event, Edge edge);
    
    Node<T> allowMerge();

}
