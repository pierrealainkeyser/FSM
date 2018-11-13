package fr.keyser.n.fsm.automat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;

public class StateBuilder implements ExtendedBuilder<StateBuilder> {

    private final List<StateBuilder> childs = new ArrayList<>();

    private final StateBuilder parent;

    private final State state;

    private final List<Supplier<? extends TransitionSource>> transitions = new ArrayList<>();

    private StateType type;

    private StateBuilder(StateBuilder parent, State state, StateType type) {
	this.parent = parent;
	if (this.parent != null) {
	    if (StateType.PLAIN == parent.type)
		parent.type = StateType.COMPOSITE;
	    this.parent.childs.add(this);
	}
	this.state = state;
	this.type = type;
    }

    StateBuilder(String state, StateType type) {
	this(null, new State(state), type);
    }

    Stream<StateNode> build() {
	Stream.Builder<StateNode> builder = Stream.builder();
	build(null, builder);
	return builder.build();
    }

    private void build(StateNode parent, Stream.Builder<StateNode> builder) {
	StateNode node = new StateNode(type, parent, state,
	        transitions.stream().map(Supplier::get).collect(Collectors.toList()));
	builder.add(node);
	childs.forEach(c -> c.build(node, builder));
    }

    StateBuilder first() {
	if (StateType.COMPOSITE == type && !childs.isEmpty())
	    return childs.get(0).first();
	else
	    return this;
    }

    public State getState() {
	return state;
    }

    public StateBuilder joining(String state) {
	return state(state, StateType.JOINING);
    }

    public void self(String key) {
	onEvent(key, this);
    }

    @Override
    public StateBuilder state(String state, StateType type) {
	if (StateType.ORTHOGONAL != this.type && StateType.JOINING == type)
	    throw new IllegalArgumentException("joining state must have an orthogonal parent");

	if (StateType.CHOICE == this.type)
	    throw new IllegalArgumentException("choice node can not have substate");

	return new StateBuilder(this, this.state.sub(state), type);
    }

    public StateBuilder on(Supplier<? extends TransitionSource> transition) {
	assertTransitions(transition);
	transitions.add(transition);
	return this;
    }

    public static Function<StateBuilder, Supplier<? extends TransitionSource>> timeout() {
	return TimedOutPreTransitionBuilder::new;
    }

    public static Function<StateBuilder, Supplier<? extends TransitionSource>> join() {
	return JoiningPreTransitionBuilder::new;
    }

    public static Function<StateBuilder, Supplier<? extends TransitionSource>> event(String evt) {
	return d -> new KeyedPreTransitionBuilder(evt, d);
    }

    public StateBuilder on(Function<StateBuilder, Supplier<? extends TransitionSource>> transition, StateBuilder destination) {
	return on(transition.apply(destination));
    }

    public StateBuilder onEvent(String key, StateBuilder destination) {
	return on(event(key), destination);
    }

    private void assertTransitions(Supplier<? extends TransitionSource> transition) {
	if (transition instanceof JoiningPreTransitionBuilder) {
	    if (StateType.ORTHOGONAL != type) {
		throw new IllegalArgumentException("state " + state + "/" + type + " doesn't allow joining transition");
	    } else
		return;
	}

	if (transition instanceof ChoiceTransitionSourceBuilder) {
	    if (StateType.CHOICE != type) {
		throw new IllegalArgumentException("state " + state + "/" + type + " doesn't allow choice transition");
	    } else
		return;
	}

	if (StateType.TERMINAL == type || StateType.JOINING == type || StateType.ORTHOGONAL == type || StateType.CHOICE == type)
	    throw new IllegalArgumentException("state " + state + "/" + type + " doesn't allow transition");
    }
}