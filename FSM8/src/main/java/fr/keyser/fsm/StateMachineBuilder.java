package fr.keyser.fsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StateMachineBuilder<S, E> {

    public static <S, E> Supplier<StateMachineBuilder<S, E>> withExecutor(Executor executor) {
	return () -> new StateMachineBuilder<S, E>().executor(executor);

    }

    public static class TransitionBuilder<S, E> {
	private TransitionBuilder(E event, State<S> destination) {
	    this.event = event;
	    this.destination = destination;
	}

	private final E event;

	private final State<S> destination;

	private List<TransitionGuard<E>> guards;

	private List<OnTransitionAction<S, E>> transitions;

	public TransitionBuilder<S, E> onTransition(OnTransitionAction<S, E> onTransition) {
	    if (transitions == null)
		transitions = new ArrayList<>();
	    transitions.add(onTransition);
	    return this;
	}

	public TransitionBuilder<S, E> guard(TransitionGuard<E> guard) {
	    if (guards == null)
		guards = new ArrayList<>();
	    guards.add(guard);
	    return this;
	}

	public TransitionBuilder<S, E> onTransition(OnSimpleTransitionAction<E> action) {
	    return onTransition((s, e, d) -> action.onTransition(e));
	}

	public TransitionBuilder<S, E> onTransition(SimpleAction action) {
	    return onTransition((s, e, d) -> action.run());
	}

	NodeTransition<S, E> build() {
	    return new NodeTransition<>(destination, transitions,
	            guards == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(guards)));
	}

	E getEvent() {
	    return event;
	}

    }

    public static class StateBuilder<S, E> {

	private final StateMachineBuilder<S, E> builder;

	private final State<S> parent;
	private final State<S> state;

	private List<OnEntryAction<E>> entries;

	private List<OnExitAction<E>> exits;

	private List<TransitionGuard<E>> guards;

	private final List<TransitionBuilder<S, E>> transitions = new ArrayList<>();

	private StateBuilder(StateMachineBuilder<S, E> builder, State<S> parent, State<S> state) {
	    this.builder = builder;
	    this.parent = parent;
	    this.state = state;
	}

	public StateBuilder<S, E> guard(TransitionGuard<E> guard) {
	    if (guards == null)
		guards = new ArrayList<>();
	    guards.add(guard);
	    return this;
	}

	public StateBuilder<S, E> onEntry(SimpleAction onEntry) {
	    return onEntry(e -> onEntry.run());
	}

	public StateBuilder<S, E> onEntry(OnEntryAction<E> onEntry) {
	    if (entries == null)
		entries = new ArrayList<>();
	    entries.add(onEntry);
	    return this;
	}

	public StateBuilder<S, E> onExit(SimpleAction onExit) {
	    return onExit(e -> onExit.run());
	}

	public StateBuilder<S, E> onExit(OnExitAction<E> onExit) {
	    if (exits == null)
		exits = new ArrayList<>();
	    exits.add(onExit);
	    return this;
	}

	public TransitionBuilder<S, E> selfTransition(E event) {
	    return transition(event, (State<S>) null);
	}

	public TransitionBuilder<S, E> transition(E event, StateBuilder<S, E> destination) {
	    return transition(event, destination.state);
	}

	private TransitionBuilder<S, E> transition(E event, State<S> destination) {
	    TransitionBuilder<S, E> tb = new TransitionBuilder<>(event, destination);
	    transitions.add(tb);
	    return tb;
	}

	public StateBuilder<S, E> sub(S sub) {
	    return builder.state(state, state.subState(sub));
	}

	NodeState<S, E> build() {
	    return new NodeState<>(state, transitions.stream().collect(transitionMapper()),
	            entries, exits, guards == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(guards)));
	}
    }

    private static <S, E> Collector<TransitionBuilder<S, E>, ?, Map<E, NodeTransition<S, E>>> transitionMapper() {
	return Collectors.toMap(TransitionBuilder::getEvent, TransitionBuilder::build);
    }

    private final Map<State<S>, StateBuilder<S, E>> builders = new LinkedHashMap<>();

    private final DelegatedDelayedEventConsumer<S, E> eventConsumer = new DelegatedDelayedEventConsumer<>();

    private Executor executor;

    private final List<TransitionBuilder<S, E>> transitions = new ArrayList<>();

    public StateBuilder<S, E> state(S state) {
	return state(null, new State<>(state));
    }

    public TransitionBuilder<S, E> selfTransition(E event) {
	return transition(event, (State<S>) null);
    }

    public TransitionBuilder<S, E> transition(E event, StateBuilder<S, E> destination) {
	return transition(event, destination.state);
    }

    private TransitionBuilder<S, E> transition(E event, State<S> destination) {
	TransitionBuilder<S, E> tb = new TransitionBuilder<>(event, destination);
	transitions.add(tb);
	return tb;
    }

    public DelayedEventConsumer<S, E> eventConsummer() {
	return eventConsumer;
    }

    private StateBuilder<S, E> state(State<S> parent, State<S> state) {
	if (builders.containsKey(state))
	    throw new IllegalStateException("State already exist : " + state);

	StateBuilder<S, E> builder = new StateBuilder<S, E>(this, parent, state);
	builders.put(state, builder);
	return builder;
    }

    private static <T> BinaryOperator<T> throwingMerger() {
	return (u, v) -> {
	    throw new IllegalStateException(String.format("Duplicate key %s", u));
	};
    }

    private NodeState<S, E> buildRoot() {
	NodeState<S, E> root = new NodeState<>(null, transitions.stream().collect(transitionMapper()), null, null, Collections.emptyList());

	Map<State<S>, NodeState<S, E>> alls = Collections
	        .unmodifiableMap(builders.entrySet().stream()
	                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().build(), throwingMerger(), LinkedHashMap::new)));
	root.setChildrens(alls);

	Map<State<S>, List<NodeState<S, E>>> childrens = new HashMap<>();

	for (NodeState<S, E> n : alls.values()) {

	    StateBuilder<S, E> buildedBy = builders.get(n.getState());
	    State<S> parent = buildedBy.parent;
	    n.setParent(alls.getOrDefault(parent, root));

	    if (parent != null) {
		List<NodeState<S, E>> parentChild = childrens.getOrDefault(parent, new ArrayList<>());
		parentChild.add(n);
		childrens.put(parent, parentChild);
	    }
	}

	for (NodeState<S, E> n : alls.values()) {
	    List<NodeState<S, E>> childs = childrens.get(n.getState());
	    if (childs != null) {
		Map<State<S>, NodeState<S, E>> um = Collections
		        .unmodifiableMap(childs.stream()
		                .collect(Collectors.toMap(NodeState::getState, Function.identity(), throwingMerger(), LinkedHashMap::new)));
		n.setChildrens(um);
	    }
	}

	return root;
    }

    public StateMachine<S, E> build() {
	StateMachine<S, E> stateMachine = new StateMachine<>(executor == null ? new SequentialExecutor() : executor, buildRoot());
	eventConsumer.setDelegated(stateMachine);
	return stateMachine;
    }

    public StateMachineBuilder<S, E> executor(Executor executor) {
	this.executor = executor;
	return this;
    }
}
