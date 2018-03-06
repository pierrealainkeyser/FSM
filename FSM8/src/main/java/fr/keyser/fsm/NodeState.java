package fr.keyser.fsm;

import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

class NodeState<S, E> {

    class Route implements Supplier<RoutingStatus<S, E>> {

	final NodeState<S, E> destination;

	final Event<E> event;

	final NodeTransition<S, E> transition;

	Route(State<S> current, Event<E> event, Optional<NodeTransition<S, E>> opt) {
	    this.event = event;

	    if (opt.isPresent()) {
		transition = opt.get();
		destination = getFirstChildOrSelf(transition.computeDestination(current));
	    } else {
		transition = null;
		destination = null;
	    }
	}

	private void clearSameNodes(List<NodeState<S, E>> entryStack, List<NodeState<S, E>> exitStack) {
	    Iterator<NodeState<S, E>> itEntry = entryStack.iterator();
	    Iterator<NodeState<S, E>> itExit = exitStack.iterator();

	    while (itExit.hasNext()) {
		if (itEntry.hasNext()) {
		    State<S> entryState = itEntry.next().state;
		    State<S> exitState = itExit.next().state;
		    if (entryState.equals(exitState)) {
			itEntry.remove();
			itExit.remove();
		    } else
			break;
		} else
		    break;

	    }
	}

	@Override
	public RoutingStatus<S, E> get() {
	    NodeState<S, E> me = NodeState.this;
	    State<S> from = me.state;
	    E eventValue = event.getValue();

	    if (transition == null)
		return RoutingStatus.noRouteFound(from, eventValue);

	    try {
		boolean valid = me.validate(event);
		if (!valid)
		    return RoutingStatus.invalidEvent(from, eventValue);

		boolean validTransition = transition.validate(event);
		if (!validTransition)
		    return RoutingStatus.invalidEvent(from, eventValue);

		List<NodeState<S, E>> entryStack = destination.collect();
		List<NodeState<S, E>> exitStack = collect();

		clearSameNodes(entryStack, exitStack);
		Collections.reverse(exitStack);

		State<S> to = destination.state;
		for (NodeState<S, E> r : exitStack)
		    r.fireExit(event);

		transition.fireTransition(from, event, to);

		for (NodeState<S, E> r : entryStack)
		    r.fireEntry(event);

		return RoutingStatus.done(from, to, eventValue);
	    } catch (RuntimeException re) {
		throw re;
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }
	}
    }

    private Map<State<S>, NodeState<S, E>> childrens = Collections.emptyMap();

    private final List<OnEntryAction<E>> onEntry;

    private final List<OnExitAction<E>> onExit;

    private NodeState<S, E> parent;

    private final State<S> state;

    private final Map<E, NodeTransition<S, E>> transitions;

    private final List<TransitionGuard<E>> guards;

    public NodeState(State<S> state, Map<E, NodeTransition<S, E>> transitions, List<OnEntryAction<E>> onEntry,
            List<OnExitAction<E>> onExit, List<TransitionGuard<E>> guards) {
	this.state = state;
	this.transitions = transitions;
	this.onEntry = onEntry;
	this.onExit = onExit;
	this.guards = guards;
    }

    private void addParentThenMe(List<NodeState<S, E>> all) {
	if (parent != null)
	    parent.addParentThenMe(all);

	// ignore root
	if (state != null)
	    all.add(this);
    }

    public RoutingStatus<S, E> fireEntry() {
	if (onEntry != null) {
	    try {
		for (OnEntryAction<E> a : onEntry)
		    a.onEntry(null);
	    } catch (RuntimeException re) {
		throw re;
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }
	}
	return RoutingStatus.done(null, state, null);
    }

    private boolean validate(Event<E> event) throws Exception {
	boolean valid = true;
	for (TransitionGuard<E> g : guards) {
	    valid = g.validate(event);
	    if (!valid)
		break;
	}
	return valid;
    }

    private List<NodeState<S, E>> collect() {
	List<NodeState<S, E>> all = new ArrayList<>();
	addParentThenMe(all);
	return all;
    }

    Optional<NodeTransition<S, E>> findTransition(E event) {
	Optional<NodeTransition<S, E>> current = Optional.ofNullable(transitions.get(event));

	if (current.isPresent() || parent == null)
	    return current;

	return parent.findTransition(event);

    }

    private void fireEntry(Event<E> event) throws Exception {
	if (onEntry != null) {
	    for (OnEntryAction<E> a : onEntry)
		a.onEntry(event);
	}
    }

    private void fireExit(Event<E> event) throws Exception {
	if (onExit != null) {
	    for (OnExitAction<E> a : onExit)
		a.onExit(event);
	}
    }

    public NodeState<S, E> firstChildOrSelf() {
	if (childrens.isEmpty())
	    return this;
	else
	    return childrens.values().iterator().next().firstChildOrSelf();
    }

    public List<State<S>> getChildrens() {
	return childrens.values().stream().map(n -> n.state).collect(toCollection(ArrayList::new));
    }

    public NodeState<S, E> getFirstChildOrSelf(State<S> state) {
	return lookup(state).firstChildOrSelf();
    }

    public List<State<S>> getStates() {
	return root().getChildrens();
    }

    public boolean isHierarchical() {
	return childrens.size() > 1;
    }

    public boolean isTerminal() {
	return transitions.isEmpty();
    }

    public NodeState<S, E> lookup(State<S> state) {
	return root().childrens.get(state);
    }

    public RoutingStatus<S, E> onEvent(Event<E> event) {
	return new Route(state, event, findTransition(event.getValue())).get();
    }

    public NodeState<S, E> root() {
	if (parent == null)
	    return this;
	else
	    return parent.root();
    }

    void setChildrens(Map<State<S>, NodeState<S, E>> childrens) {
	this.childrens = childrens;
    }

    void setParent(NodeState<S, E> parent) {
	this.parent = parent;
    }

    State<S> getState() {
	return state;
    }
}
