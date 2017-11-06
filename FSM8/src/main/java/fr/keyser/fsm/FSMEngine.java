package fr.keyser.fsm;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

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

	private final C context;

	private S current;

	private AtomicInteger processing = new AtomicInteger(0);

	private BlockingQueue<FSMEvent<E>> events = new LinkedBlockingQueue<>();

	private boolean done;

	private FSMState<S, C> stateWrapper = new FSMState<S, C>() {

	    @Override
	    public S getCurrentState() {
		return FSMInstanceEngine.this.getCurrentState();
	    }

	    @Override
	    public C getContext() {
		return FSMInstanceEngine.this.getContext();
	    }

	    @Override
	    public boolean isDone() {
		return FSMInstanceEngine.this.isDone();
	    }
	};

	FSMInstanceEngine(C context, S current, boolean doEnter) throws FSMException {
	    this.context = context;
	    this.current = current;

	    // permet de vérifier que l'état existe bien
	    State<S, E, C> state = lookup(current);

	    if (doEnter) {
		handleDone(state);
		processEnter(state, null);
	    } else
		handleDone(state);
	}

	private void processEvent(FSMEvent<E> evt) throws FSMException {
	    if (isDone())
		throw new FSMDoneException();

	    E event = evt.getEvent();

	    State<S, E, C> source = lookup(current);

	    Transition<S, E, C> transition = source.lookup(event);
	    S dest = transition.getDestination();
	    State<S, E, C> destination = lookup(dest);

	    if (listeners != null) {
		for (FSMListener<S, E, C> listener : listeners)
		    listener.willTransit(stateWrapper, evt, destination.getState());
	    }

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

	    if (listeners != null) {
		for (FSMListener<S, E, C> listener : listeners)
		    listener.stateReached(stateWrapper);
	    }
	}

	@Override
	public void sendEvent(E event, Object... args) throws FSMException {
	    FSMEvent<E> evt = new FSMEvent<>(event, args);
	    if (isDone())
		fireException(evt, new FSMDoneException());

	    if (event == null)
		fireException(evt, new FSMNullEventException());

	    this.events.add(evt);
	    if (processing.getAndIncrement() == 0) {

		FSMEvent<E> toProcess = null;
		try {
		    do {
			toProcess = this.events.poll();
			processEvent(toProcess);
		    } while (processing.decrementAndGet() > 0);
		} catch (FSMException exception) {
		    events.clear();
		    processing.set(0);

		    fireException(toProcess, exception);
		}
	    }
	}

	private void fireException(FSMEvent<E> toProcess, FSMException exception) {
	    if (listeners != null) {
		for (FSMListener<S, E, C> listener : listeners)
		    listener.exceptionThrowed(stateWrapper, toProcess, exception);
	    }
	    throw exception;
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

    }

    private final S initial;

    private final Map<S, State<S, E, C>> states;

    private final List<FSMListener<S, E, C>> listeners;

    State<S, E, C> lookup(S state) throws FSMException {
	if (state == null)
	    throw new FSMNullStateException();

	if (states.containsKey(state))
	    return states.get(state);

	throw new FSMNoSuchStateException(state.toString());
    }

    FSMEngine(S initial, Collection<State<S, E, C>> states, List<FSMListener<S, E, C>> listeners) {
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

    @Override
    public FSMInstance<S, E, C> start(C context) throws FSMException {
	return new FSMInstanceEngine(context, initial, true);
    }

    @Override
    public FSMInstance<S, E, C> resume(FSMState<S, C> state) throws FSMException {
	return new FSMInstanceEngine(state.getContext(), state.getCurrentState(), false);
    }

}
