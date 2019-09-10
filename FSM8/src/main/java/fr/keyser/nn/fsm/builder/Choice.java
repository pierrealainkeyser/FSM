package fr.keyser.nn.fsm.builder;

import fr.keyser.nn.fsm.impl.ChoicePredicate;

public interface Choice<T> extends Edge {

    public Choice<T> when(ChoicePredicate<T> filter, Edge edge);

    public Choice<T> otherwise(Edge edge);

}
