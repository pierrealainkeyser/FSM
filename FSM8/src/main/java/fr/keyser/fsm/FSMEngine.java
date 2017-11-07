package fr.keyser.fsm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.keyser.fsm.exception.FSMDoneException;
import fr.keyser.fsm.exception.FSMExecutionException;
import fr.keyser.fsm.exception.FSMNoSuchStateException;
import fr.keyser.fsm.exception.FSMNullEventException;
import fr.keyser.fsm.exception.FSMNullStateException;

/**
 * L'implémentation de base d'un moteur {@link FSM} suivant le patron "poid
 * mouche"
 * 
 * @author pakeyser
 *
 * @param <S>
 *            le type des états
 * @param <E>
 *            le type des évènements
 * @param <C>
 *            le type du context
 */
class FSMEngine<S, E, C> implements FSM<S, E, C> {

    /**
     * Permet de voir un coup devant l'execution
     * 
     * @author pakeyser
     *
     */
    private class StateAheadInstance implements FSMInstance<S, E, C> {

	private final FSMInstance<S, E, C> delegated;

	private final State<S, E, C> destination;

	public StateAheadInstance(FSMInstance<S, E, C> delegated, State<S, E, C> destination) {
	    this.delegated = delegated;
	    this.destination = destination;
	}

	@Override
	public void sendEvent(E event, Object... args) throws FSMException {
	    delegated.sendEvent(event, args);
	}

	@Override
	public void execute(Runnable command) {
	    delegated.execute(command);
	}

	@Override
	public FSMInstanceKey getKey() {
	    return delegated.getKey();
	}

	@Override
	public long getTransitionCount() {
	    return delegated.getTransitionCount();
	}

	@Override
	public C getContext() {
	    return delegated.getContext();
	}

	@Override
	public S getCurrentState() {
	    return destination.getState();
	}

	@Override
	public boolean isDone() {
	    return destination.isTerminal();
	}
    }

    /**
     * L'implémentation de base, utilise une Queue pour les problémes de récurvisité
     * 
     * @author pakeyser
     *
     */
    private class FSMInstanceEngine implements FSMInstance<S, E, C> {

	private final Executor executor;

	private final C context;

	private S current;

	private boolean done;

	private long transitionCount;

	private FSMState<S, C> stateWrapper = new FSMStateWrapper<>(this);

	private final List<FSMListener<S, E, C>> instanceListeners;

	private final FSMInstanceKey key;

	FSMInstanceEngine(FSMInstanceKey key, Executor executor, C context, S current, long transitionCount, boolean doEnter,
		List<FSMListener<S, E, C>> instanceListeners) throws FSMException {
	    this.key = key;
	    this.context = context;
	    this.current = current;
	    this.executor = executor;
	    this.instanceListeners = instanceListeners != null ? Collections.unmodifiableList(instanceListeners) : null;

	    // permet de vérifier que l'état existe bien
	    State<S, E, C> state = lookup(current);

	    handleDone(state);
	    this.transitionCount = transitionCount;
	    if (doEnter)
		processEnter(state, null);

	}

	private void processEvent(FSMEvent<E> evt) throws FSMException {
	    if (isDone())
		throw new FSMDoneException();

	    E event = evt.getEvent();

	    State<S, E, C> source = lookup(current);

	    Transition<S, E, C> transition = source.lookup(event);
	    S dest = transition.getDestination();
	    State<S, E, C> destination = lookup(dest);

	    listeners(l -> l.willTransit(stateWrapper, evt, dest));

	    boolean sameState = current.equals(dest);

	    // gestion de la sortie
	    if (!sameState)
		processExit(source, evt);

	    // gestion de la transition
	    processTransition(transition, evt);

	    // gestion de l'entrée
	    processEnter(destination, evt);

	    // mise en place de l'état
	    handleDone(destination);
	}

	private void processTransition(Transition<S, E, C> transition, FSMEvent<E> event) {
	    OnTransitionAction<S, E, C> onTransition = transition.getOnTransition();
	    if (onTransition != null) {
		try {
		    onTransition.onTransition(stateWrapper, event);
		} catch (Exception e) {
		    throw new FSMExecutionException(e);
		}
	    }
	}

	private void processExit(State<S, E, C> source, FSMEvent<E> event) {
	    OnExitAction<S, E, C> onExit = source.getOnExit();
	    if (onExit != null) {
		try {
		    onExit.onExit(stateWrapper, event);
		} catch (Exception e) {
		    throw new FSMExecutionException(e);
		}
	    }
	}

	private void processEnter(State<S, E, C> destination, FSMEvent<E> event) throws FSMExecutionException {
	    OnEnterAction<S, E, C> onEnter = destination.getOnEnter();
	    if (onEnter != null) {
		try {
		    onEnter.onEnter(new StateAheadInstance(this, destination), event);
		} catch (Exception e) {
		    throw new FSMExecutionException(e);
		}
	    }

	}

	private void handleDone(State<S, E, C> destination) {
	    this.current = destination.getState();
	    this.done = destination.isTerminal();
	    ++this.transitionCount;

	    listeners(l -> l.stateReached(stateWrapper));
	}

	@Override
	public void sendEvent(E event, Object... args) throws FSMException {
	    FSMEvent<E> evt = new FSMEvent<>(event, args);
	    if (isDone())
		fireException(evt, new FSMDoneException(), true);

	    if (event == null)
		fireException(evt, new FSMNullEventException(), true);

	    execute(() -> {
		try {
		    processEvent(evt);
		} catch (FSMException exception) {
		    fireException(evt, exception, false);
		}
	    });
	}

	public void execute(Runnable command) {
	    executor.execute(command);
	}

	private void fireException(FSMEvent<E> toProcess, FSMException exception, boolean doThrow) {
	    listeners(l -> l.exceptionThrowed(stateWrapper, toProcess, exception));
	    if (doThrow)
		throw exception;
	}

	private void listeners(Consumer<FSMListener<S, E, C>> action) {
	    if (listeners != null)
		listeners.forEach(action);

	    if (instanceListeners != null)
		instanceListeners.forEach(action);
	}

	@Override
	public boolean isDone() {
	    return done;
	}

	@Override
	public C getContext() {
	    return context;
	}

	@Override
	public S getCurrentState() {
	    return current;
	}

	@Override
	public long getTransitionCount() {
	    return transitionCount;
	}

	@Override
	public FSMInstanceKey getKey() {
	    return key;
	}
    }

    private final Supplier<Executor> executorFactory;

    private final S initial;

    private final Map<S, State<S, E, C>> states;

    private final List<FSMListener<S, E, C>> listeners;

    private final Supplier<FSMInstanceKey> keyFactory;

    State<S, E, C> lookup(S state) throws FSMException {
	if (state == null)
	    throw new FSMNullStateException();

	if (states.containsKey(state))
	    return states.get(state);

	throw new FSMNoSuchStateException(state.toString());
    }

    FSMEngine(Supplier<FSMInstanceKey> keyFactory, Supplier<Executor> executorFactory, S initial, Collection<State<S, E, C>> states,
	    List<FSMListener<S, E, C>> listeners) {
	this.keyFactory = keyFactory;
	this.executorFactory = executorFactory;
	this.initial = initial;
	this.listeners = listeners != null ? Collections.unmodifiableList(listeners) : null;

	Map<S, State<S, E, C>> newStates = new LinkedHashMap<S, State<S, E, C>>();
	for (State<S, E, C> state : states)
	    newStates.put(state.getState(), state);
	this.states = Collections.unmodifiableMap(newStates);
    }

    @Override
    public void visit(FSMVisitor<S, E> visitor) {
	State<S, E, C> init = states.get(initial);
	if (init != null) {
	    visitor.initial(init.getState(), init.getOnEnter(), init.getOnExit());
	    init.visit(visitor);
	}

	for (State<S, E, C> state : states.values()) {
	    visitor.state(state.getState(), state.getOnEnter(), state.getOnExit(), state.isTerminal());
	    state.visit(visitor);
	}
    }

    private class InstanceBuilder implements FSMInstanceBuilder<S, E, C> {

	private List<FSMListener<S, E, C>> listeners;

	@Override
	public FSMInstanceBuilder<S, E, C> listener(FSMListener<S, E, C> listener) {
	    if (listeners == null)
		listeners = new ArrayList<>();
	    listeners.add(listener);
	    return this;
	}

	private List<FSMListener<S, E, C>> listeners() {
	    if (listeners != null)
		return new ArrayList<>(listeners);
	    else
		return null;
	}

	@Override
	public FSMInstance<S, E, C> start(C context) throws FSMException {
	    return new FSMInstanceEngine(keyFactory.get(), executorFactory.get(), context, initial, 0l, true, listeners());
	}

	@Override
	public FSMInstance<S, E, C> resume(FSMState<S, C> state) throws FSMException {
	    return new FSMInstanceEngine(keyFactory.get(), executorFactory.get(), state.getContext(), state.getCurrentState(),
		    state.getTransitionCount(),
		    false, listeners());
	}

    }

    @Override
    public FSMInstanceBuilder<S, E, C> instance() {
	return new InstanceBuilder();
    }
}
