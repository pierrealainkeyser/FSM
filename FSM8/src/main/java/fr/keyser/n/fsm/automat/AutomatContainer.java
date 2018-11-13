package fr.keyser.n.fsm.automat;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.EventReceiver;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;
import fr.keyser.n.fsm.listener.AutomatListener;
import fr.keyser.n.fsm.listener.DelegatedAutomatListener;

public class AutomatContainer implements EventReceiver {

    private final Automat automat;

    private final Executor executor;

    private final Supplier<InstanceId> idSupplier;

    private final EventReceiver innerDispatch = new EventReceiver() {

	@Override
	public void receive(Event event) {
	    List<AutomatInstance> selected = prepareTargets(event);
	    selected.forEach(ai -> {
		if (listener.guard(ai.getId(), event))
		    ai.receive(event);
	    });
	}
    };

    private final Map<InstanceId, AutomatInstance> instances = new LinkedHashMap<>();

    private final DelegatedAutomatListener listener;

    public AutomatContainer(Executor executor, Automat automat, AutomatListener listener) {
	this(executor, automat, listener, new SequenceInstanceIdSupplier());
    }

    public AutomatContainer(Executor executor, Automat automat, AutomatListener listener, Supplier<InstanceId> idSupplier) {
	this.executor = executor;
	this.automat = automat;
	this.idSupplier = idSupplier;
	this.listener = new DelegatedAutomatListener(listener) {
	    @Override
	    public boolean guard(InstanceId id, Event evt) {
		AutomatInstance ai = instances.get(id);
		if (ai == null)
		    return false;

		StateType type = typeOf(ai);
		// no event on joining state
		if (StateType.JOINING == type)
		    return false;

		// only Joined event on orthogonal state
		if (StateType.ORTHOGONAL == type)
		    return evt instanceof Joined;

		return super.guard(id, evt);
	    }

	    @Override
	    public void reaching(InstanceId id, State reached, StateType type) {
		super.reaching(id, reached, type);

		if (StateType.ORTHOGONAL == type) {
		    startAllChilds(id, reached);
		} else if (StateType.JOINING == type) {
		    checkJoinedState(id, reached);
		} else if (StateType.TERMINAL == type) {
		    terminateInstance(id);
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
	    parentInstance.receive(Joined.INSTANCE);
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

    @Override
    public void receive(Event evt) {
	executor.execute(() -> innerDispatch.receive(evt));
    }

    private void removeInstance(AutomatInstance ai) {
	terminateInstance(ai.getId());
    }

    public void start() {
	startInstance(null, automat.getInitial());
    }

    private void startAllChilds(InstanceId parentId, State reached) {
	Stream<State> childs = automat.orthogonalChilds(reached);
	childs.forEach(s -> startInstance(parentId, s));
    }

    private void startInstance(InstanceId parentId, State state) {
	AutomatInstance ai = new AutomatInstance(parentId, automat, state, idSupplier.get(), listener);
	instances.put(ai.getId(), ai);
	listener.starting(ai.getInstanceState());
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
