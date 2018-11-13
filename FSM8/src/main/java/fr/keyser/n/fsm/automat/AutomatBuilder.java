package fr.keyser.n.fsm.automat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;

public class AutomatBuilder implements ExtendedBuilder<StateBuilder> {

    private static <T> BinaryOperator<T> throwingMerger() {
	return (u, v) -> {
	    throw new IllegalStateException(String.format("Duplicate key %s", u));
	};
    }

    private final List<StateBuilder> roots = new ArrayList<>();

    public Automat build() {
	if (roots.isEmpty())
	    return null;

	StateBuilder initial = roots.get(0);

	LinkedHashMap<State, StateNode> collected = roots.stream().flatMap(StateBuilder::build)
	        .collect(Collectors.toMap(StateNode::getState, s -> s, throwingMerger(), LinkedHashMap::new));

	return new Automat(initial.first().getState(), collected);

    }

    @Override
    public StateBuilder state(String state, StateType type) {
	if (StateType.JOINING == type) {
	    throw new IllegalArgumentException("joining state must have an orthogonal parent");
	}

	StateBuilder sb = new StateBuilder(state, type);
	roots.add(sb);
	return sb;
    }

}
