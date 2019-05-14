package fr.keyser.n.fsm.automat;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.EventProcessingStatus;
import fr.keyser.n.fsm.EventReceiver;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;
import fr.keyser.n.fsm.listener.AutomatListener;
import fr.keyser.n.fsm.listener.DelegatedAutomatListener;

public class AutomatContainer implements EventReceiver {

    public static final String INDEX = "index";

    private final Automat automat;

    private final AutomatExecutor executor = new AutomatExecutor();

    private final Supplier<InstanceId> idSupplier;

    private final Map<InstanceId, AutomatInstance> instances = new LinkedHashMap<>();

    private final DelegatedAutomatListener listener;

    public AutomatContainer(Automat automat, AutomatListener listener) {
	this(automat, listener, new SequenceInstanceIdSupplier());
    }

    public AutomatContainer(Automat automat, AutomatListener listener, Supplier<InstanceId> idSupplier) {
	this.automat = automat;
	this.idSupplier = idSupplier;
	this.listener = new DelegatedAutomatListener(listener) {
	    @Override
	    public boolean guard(InstanceState state, Event evt) {
		AutomatInstance ai = instances.get(state.getId());
		if (ai == null)
		    return false;

		StateType type = typeOf(ai);
		// no event on joining state
		if (StateType.JOINING == type)
		    return false;

		// only Joined event on orthogonal state
		if (StateType.ORTHOGONAL == type)
		    return evt instanceof Joined;

		return super.guard(state, evt);
	    }

	    @Override
	    public void reaching(InstanceState state, State reached, StateType type) {
		super.reaching(state, reached, type);

		if (StateType.ORTHOGONAL == type) {
		    startAllChilds(state.getId(), reached);
		} else if (StateType.JOINING == type) {
		    checkJoinedState(state.getId(), reached);
		} else if (StateType.TERMINAL == type) {
		    terminateInstance(state.getId());
		}
	    }
	};
    }

    private void checkJoinedState(InstanceId id, State reached) {
	AutomatInstance current = instances.get(id);
	InstanceId parentId = current.getParentId();
	AutomatInstance parentInstance = instances.get(parentId);

	long expected = automat.orthogonalChilds(parentInstance.getCurrent()).count();
	List<AutomatInstance> alreadyJoined = instances.values().stream()
	        .filter(joiningWithParent(parentId))
	        .collect(toList());

	if (expected == alreadyJoined.size()) {
	    // remove all joined instance
	    alreadyJoined.forEach(this::removeInstance);

	    // continue on the parent
	    receive(Joined.join(parentId));
	}
    }

    public List<InstanceState> getStates() {
	return instances.values().stream()
	        .map(AutomatInstance::getInstanceState)
	        .collect(toList());
    }

    public boolean isDone() {
	return instances.isEmpty();
    }

    private Predicate<AutomatInstance> joiningWithParent(InstanceId parent) {
	return ai -> StateType.JOINING == typeOf(ai) && parent.equals(ai.getParentId());
    }

    private List<AutomatInstance> prepareTargets(Event event) {
	InstanceId targetId = event.getId();
	List<AutomatInstance> selected = null;
	if (targetId != null) {
	    AutomatInstance instance = instances.get(targetId);
	    if (instance != null)
		selected = Collections.singletonList(instance);
	    else
		selected = Collections.emptyList();
	} else
	    selected = new ArrayList<>(instances.values());
	return selected;
    }

    private void innerDispatch(AtomicReference<EventProcessingStatus> ref, Event event) {

	// mise en place du status
	EventProcessingStatus status = executor.getStatus();

	List<AutomatInstance> selected = prepareTargets(event);
	selected.forEach(ai -> {
	    if (listener.guard(ai.getInstanceState(), event))
		ai.receive(status, event);
	});

	ref.set(status);
    }

    @Override
    public EventProcessingStatus receive(Event evt) {
	AtomicReference<EventProcessingStatus> ref = new AtomicReference<>();
	executor.execute(() -> innerDispatch(ref, evt));
	return ref.get();
    }

    private void removeInstance(AutomatInstance ai) {
	terminateInstance(ai.getId());
    }

    public void start() {
	startInstance(null, automat.getInitial(), null);
    }

    private void startAllChilds(InstanceId parentId, State reached) {
	Stream<State> childs = automat.orthogonalChilds(reached);

	AtomicInteger index = new AtomicInteger(0);
	childs.forEach(s -> {
	    startInstance(parentId, s, index);
	    index.incrementAndGet();
	});
    }

    private void startInstance(InstanceId parentId, State state, AtomicInteger index) {
	AutomatInstance ai = new AutomatInstance(parentId, automat, state, idSupplier.get(), listener);
	instances.put(ai.getId(), ai);
	InstanceState instanceState = ai.getInstanceState();
	if (index != null)
	    instanceState.getProps().put(INDEX, index.get());

	listener.starting(instanceState);
	ai.start();
    }

    private void terminateInstance(InstanceId id) {
	AutomatInstance ai = instances.remove(id);
	if (ai != null)
	    listener.terminating(ai.getInstanceState());
    }

    private StateType typeOf(AutomatInstance ai) {
	return automat.type(ai.getCurrent());
    }
}
