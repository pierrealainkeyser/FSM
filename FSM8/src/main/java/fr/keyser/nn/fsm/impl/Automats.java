package fr.keyser.nn.fsm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.State;

public class Automats<T> implements EventEndpoint {

    private final static Logger logger = LoggerFactory.getLogger(Automats.class);

    private final MultiPriorityQueue<EventMsg> events = new MultiPriorityQueue<>();

    private final Graph graph;

    private final Supplier<InstanceId> idSupplier;

    private final Map<InstanceId, AutomatInstance<T>> instances = new LinkedHashMap<>();

    private final AutomatsListener<T> listener;

    private final AtomicInteger worker = new AtomicInteger(0);

    private static int compareId(InstanceId l, InstanceId r) {
	return Integer.parseInt(l.getId()) - Integer.parseInt(r.getId());
    }

    public Automats(Graph graph, AutomatsListener<T> listener, Supplier<InstanceId> idSupplier) {
	this.graph = graph;
	this.listener = listener;
	if (idSupplier == null) {
	    idSupplier = () -> {
		InstanceId i = instances.keySet().stream().max(Automats::compareId).orElse(new InstanceId("-1"));
		return new InstanceId(Integer.toString(Integer.parseInt(i.getId()) + 1));
	    };
	}

	this.idSupplier = idSupplier;
    }

    Instance<T> getInstance(InstanceId id) {
	return instances.get(id);
    }

    public List<Instance<T>> instances() {
	return new ArrayList<>(instances.values());
    }

    @Override
    public String toString() {
	return graph.toString();
    }

    List<Instance<T>> childsOf(InstanceId instanceId) {
	return instances.values().stream().filter(ai -> instanceId.equals(ai.getParentId())).collect(Collectors.toList());
    }

    private void dispatch(AutomatInstance<T> instance, EventMsg event) {
	Collection<Transition> transitions = graph.findTransitions(instance.getState(), event);
	if (transitions.isEmpty()) {
	    // no matching transition
	    logger.debug("Instance {} no matching transition for : {}", instance.getInstanceId(), event);
	    return;
	}

	Optional<Transition> first = transitions.stream().filter(t -> listener.guard(instance, t)).findFirst();
	if (first.isPresent()) {
	    follow(instance, first.get(), event);
	} else {
	    // guard has rejected all transitions
	    logger.debug("Instance {} guard rejected all transitions for : {}", instance.getInstanceId(), event);
	}
    }

    private void dispatch(EventMsg event) {

	Collection<AutomatInstance<T>> selected = prepareTargets(event);
	if (event instanceof PoisonPill) {
	    processPoissonPill(selected);
	} else if (event instanceof Start) {
	    processStart((Start) event);
	} else {
	    for (AutomatInstance<T> ai : selected) {
		if (listener.guard(ai, event)) {
		    dispatch(ai, event);
		}
	    }
	}
    }

    private void follow(AutomatInstance<T> instance, Transition transition, EventMsg event) {

	logger.debug("Instance {} follow transition : {}", instance.getInstanceId(), transition);

	Iterator<State> leaving = transition.leaving().iterator();
	while (leaving.hasNext())
	    instance = (AutomatInstance<T>) listener.leaving(instance, leaving.next(), event);

	Iterator<State> entering = transition.entering().iterator();
	while (entering.hasNext())
	    instance = (AutomatInstance<T>) listener.entering(instance, entering.next(), event);

	instance = instance.state(transition.getDestination());

	// TODO store events
	instances.put(instance.getInstanceId(), instance);
    }

    private void kill(AutomatInstance<T> instance) {
	instances.remove(instance.getInstanceId());

	listener.terminating(instance);
    }

    private Collection<AutomatInstance<T>> prepareTargets(EventMsg event) {
	InstanceId targetId = event.getTarget();
	Collection<AutomatInstance<T>> selected = null;
	if (targetId != null) {
	    AutomatInstance<T> instance = instances.get(targetId);
	    if (instance != null)
		selected = Collections.singleton(instance);
	    else
		selected = Collections.emptyList();
	} else
	    selected = new ArrayList<>(instances.values());
	return selected;
    }

    private void processEvents() {
	// run
	while (!events.isEmpty()) {
	    try {
		dispatch(events.remove());
	    } finally {
		worker.getAndDecrement();
	    }
	}
    }

    private void processPoissonPill(Collection<AutomatInstance<T>> selected) {
	for (AutomatInstance<T> ai : selected) {
	    kill(ai);
	}
    }

    private void processStart(Start start) {

	State initial = graph.initial();
	InstanceId id = idSupplier.get();
	AutomatInstance<T> instance = AutomatInstance.initial(this, id, initial, listener.start(start));

	logger.debug("Starting initial : {}", instance);

	instances.put(instance.getInstanceId(), instance);

	Iterator<State> it = initial.states().iterator();
	while (it.hasNext()) {
	    instance = (AutomatInstance<T>) listener.entering(instance, it.next(), start);
	}

	instances.put(instance.getInstanceId(), instance);
    }

    void startChild(Instance<T> parent, State childState, EventMsg start, int index) {
	// find true child to start
	State state = graph.findFirstChild(childState);

	T payload = listener.start(parent, start, index);

	InstanceId newId = idSupplier.get();
	AutomatInstance<T> child = ((AutomatInstance<T>) parent).child(newId, state, payload);

	instances.put(child.getInstanceId(), child);

	logger.debug("Starting child : {}", child);

	child = (AutomatInstance<T>) listener.entering(child, state, start);

	instances.put(child.getInstanceId(), child);

    }

    @Override
    public void submit(int priority, EventMsg event) {
	events.add(priority, event);

	if (worker.getAndIncrement() == 0) {
	    processEvents();
	}

    }

}
