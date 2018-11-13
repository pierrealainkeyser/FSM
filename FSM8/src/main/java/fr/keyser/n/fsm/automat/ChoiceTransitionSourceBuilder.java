package fr.keyser.n.fsm.automat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import fr.keyser.n.fsm.State;

public class ChoiceTransitionSourceBuilder implements Supplier<ChoiceTransitionSource> {

    private final Map<String, State> destinations = new LinkedHashMap<>();

    public static ChoiceTransitionSourceBuilder choice(String key, StateBuilder builder) {
	return new ChoiceTransitionSourceBuilder().or(key, builder);
    }

    public ChoiceTransitionSourceBuilder or(String key, StateBuilder builder) {
	destinations.put(key, builder.getState());
	return this;
    }

    public ChoiceTransitionSourceBuilder otherwise(StateBuilder builder) {
	destinations.put("<otherwise>", builder.getState());
	return this;
    }

    @Override
    public ChoiceTransitionSource get() {
	return new ChoiceTransitionSource(destinations);
    }

}
