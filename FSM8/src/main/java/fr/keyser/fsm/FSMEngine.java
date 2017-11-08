package fr.keyser.fsm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.keyser.fsm.exception.FSMDoneException;
import fr.keyser.fsm.exception.FSMExecutionException;
import fr.keyser.fsm.exception.FSMNoSuchStateException;
import fr.keyser.fsm.exception.FSMNoSuchTransition;
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

    private class FSMEventConsumer implements Runnable {

	private final FSMInstanceEngine engine;

	private final FSMEvent<E> evt;

	private final CompletableFuture<Boolean> future = new CompletableFuture<>();

	public FSMEventConsumer(FSMInstanceEngine engine, FSMEvent<E> evt) {
	    this.evt = evt;
	    this.engine = engine;
	}

	public void fireException(FSMException exception) {
	    future.complete(Boolean.FALSE);
	    engine.fireException(evt, exception);
	}

	public CompletableFuture<Boolean> getFuture() {
	    return future;
	}

	@Override
	public void run() {
	    try {
		engine.processEvent(evt);
		future.complete(Boolean.TRUE);
	    } catch (FSMException exception) {
		fireException(exception);
	    }
	}
    }

    /**
     * L'implémentation de base, utilise une Queue pour les problémes de récurvisité
     * 
     * @author pakeyser
     *
     */
    private class FSMInstanceEngine implements FSMInstance<S, E, C> {

	private final C context;

	private S current;

	private boolean done;

	private final Executor executor;

	private final List<FSMListener<S, E, C>> instanceListeners;

	private final FSMInstanceKey key;

	private FSMState<S, C> stateWrapper = new FSMStateWrapper<>(this);

	private long transitionCount;

	FSMInstanceEngine(FSMInstanceKey key, Executor executor, C context, S current, long transitionCount, boolean doEnter,
		List<FSMListener<S, E, C>> instanceListeners) throws FSMException {
	    this.key = key;
	    this.context = context;
	    this.current = current;
	    this.executor = executor;
	    this.instanceListeners = instanceListeners != null ? Collections.unmodifiableList(instanceListeners) : null;

	    // permet de vérifier que l'état existe bien
	    State<S, E, C> state = lookup(current);

	    handleDone(state, false);
	    this.transitionCount = transitionCount;
	    if (doEnter)
		processEnter(state, null);

	}

	public void execute(Runnable command) {
	    executor.execute(command);
	}

	private void fireException(FSMEvent<E> toProcess, FSMException exception) {
	    listeners(l -> l.exceptionThrowed(stateWrapper, toProcess, exception));
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
	public FSMInstanceKey getKey() {
	    return key;
	}

	@Override
	public long getTransitionCount() {
	    return transitionCount;
	}

	private void handleDone(State<S, E, C> destination, boolean stateChanged) {
	    this.current = destination.getState();
	    this.done = destination.isTerminal();

	    if (stateChanged) {
		++this.transitionCount;
		listeners(l -> l.stateReached(stateWrapper));
	    }
	}

	@Override
	public boolean isDone() {
	    return done;
	}

	private void listeners(Consumer<FSMListener<S, E, C>> action) {
	    // d'abord les écouteurs d'instances
	    if (instanceListeners != null)
		instanceListeners.forEach(action);

	    // puis les écouteurs globaux
	    if (listeners != null)
		listeners.forEach(action);
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

	private void processEvent(FSMEvent<E> evt) throws FSMException {
	    if (isDone())
		throw new FSMDoneException();

	    E event = evt.getEvent();

	    State<S, E, C> source = lookup(current);

	    Transition<S, E, C> transition = source.lookup(event).orElseGet(() -> getGlobalTransition(event));
	    S dest = transition.getDestination();
	    State<S, E, C> destination = lookup(dest);

	    listeners(l -> l.willTransit(stateWrapper, evt, dest));

	    boolean statedChanged = !current.equals(dest);

	    // gestion de la sortie
	    if (statedChanged)
		processExit(source, evt);

	    // gestion de la transition
	    processTransition(transition, evt);

	    // gestion de l'entrée
	    if (statedChanged)
		processEnter(destination, evt);

	    // mise en place de l'état
	    handleDone(destination, statedChanged);
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

	private void processTransition(Transition<S, E, C> transition, FSMEvent<E> event) {
	    OnTransitionAction<S, E, C> onTransition = transition.getOnTransition();
	    if (onTransition != null) {
		try {
		    onTransition.onTransition(this, event);
		} catch (Exception e) {
		    throw new FSMExecutionException(e);
		}
	    }
	}

	@Override
	public CompletableFuture<Boolean> sendEvent(E event, Object... args) throws FSMException {

	    FSMEvent<E> evt = new FSMEvent<>(event, args);
	    FSMEventConsumer consumer = new FSMEventConsumer(this, evt);

	    if (isDone())
		consumer.fireException(new FSMDoneException());
	    else if (event == null)
		consumer.fireException(new FSMNullEventException());
	    else
		execute(consumer);
	    return consumer.getFuture();

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
	public FSMInstance<S, E, C> resume(FSMState<S, C> state) throws FSMException {
	    return new FSMInstanceEngine(keyFactory.get(), executorFactory.get(), state.getContext(), state.getCurrentState(),
		    state.getTransitionCount(),
		    false, listeners());
	}

	@Override
	public FSMInstance<S, E, C> start(C context) throws FSMException {
	    return new FSMInstanceEngine(keyFactory.get(), executorFactory.get(), context, initial, 0l, true, listeners());
	}

    }

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
	public void execute(Runnable command) {
	    delegated.execute(command);
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
	public FSMInstanceKey getKey() {
	    return delegated.getKey();
	}

	@Override
	public long getTransitionCount() {
	    return delegated.getTransitionCount();
	}

	@Override
	public boolean isDone() {
	    return destination.isTerminal();
	}

	@Override
	public CompletableFuture<Boolean> sendEvent(E event, Object... args) {
	    return delegated.sendEvent(event, args);
	}
    }

    private final Supplier<Executor> executorFactory;

    private final Map<E, Transition<S, E, C>> globals;

    private final S initial;

    private final Supplier<FSMInstanceKey> keyFactory;

    private final List<FSMListener<S, E, C>> listeners;

    private final Map<S, State<S, E, C>> states;

    FSMEngine(Supplier<FSMInstanceKey> keyFactory, Supplier<Executor> executorFactory, S initial, Collection<State<S, E, C>> states,
	    Collection<Transition<S, E, C>> globals,
	    List<FSMListener<S, E, C>> listeners) {
	this.keyFactory = keyFactory;
	this.executorFactory = executorFactory;
	this.initial = initial;
	this.listeners = listeners != null ? Collections.unmodifiableList(listeners) : null;

	Map<S, State<S, E, C>> newStates = new LinkedHashMap<>();
	for (State<S, E, C> state : states)
	    newStates.put(state.getState(), state);
	this.states = Collections.unmodifiableMap(newStates);

	Map<E, Transition<S, E, C>> newGlobals = new LinkedHashMap<>();
	for (Transition<S, E, C> global : globals)
	    newGlobals.put(global.getEvent(), global);
	this.globals = Collections.unmodifiableMap(newGlobals);

    }

    private Transition<S, E, C> getGlobalTransition(E event) throws FSMException {
	if (globals.containsKey(event))
	    return globals.get(event);

	throw new FSMNoSuchTransition(event.toString());
    }

    @Override
    public FSMInstanceBuilder<S, E, C> instance() {
	return new InstanceBuilder();
    }

    State<S, E, C> lookup(S state) throws FSMException {
	if (state == null)
	    throw new FSMNullStateException();

	if (states.containsKey(state))
	    return states.get(state);

	throw new FSMNoSuchStateException(state.toString());
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
}
