package fr.keyser.nn.fsm.builder;

import fr.keyser.nn.fsm.impl.CreateChildFunction;
import fr.keyser.nn.fsm.impl.MergeFunction;

public interface Region<T> extends Edge, NodeContainer<T> {

    Edge joinTo(Edge edge);

    Region<T> autoJoinTo(Edge edge);

    Region<T> merge(MergeFunction<T> function);

    Region<T> create(CreateChildFunction<T> function);
}
