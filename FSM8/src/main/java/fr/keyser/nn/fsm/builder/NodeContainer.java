package fr.keyser.nn.fsm.builder;

public interface NodeContainer<T> {

    TransitionNode<T> auto(String name);

    Node<T> node(String node);

    NodeContainer<T> initial(Edge edge);
}
