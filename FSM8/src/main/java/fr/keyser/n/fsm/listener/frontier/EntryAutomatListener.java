package fr.keyser.n.fsm.listener.frontier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;
import fr.keyser.n.fsm.listener.AutomatListener;
import fr.keyser.n.fsm.listener.DelegatedAutomatListener;

public class EntryAutomatListener extends DelegatedAutomatListener {

    private final Map<State, List<EntryListener>> entries = new HashMap<>();

    private final Map<State, List<EntryListener>> reaches = new HashMap<>();

    private final Map<State, List<EntryListener>> exits = new HashMap<>();

    public EntryAutomatListener() {

    }

    public EntryAutomatListener(AutomatListener listener) {
	super(listener);
    }

    public EntryAutomatListener entry(State state, EntryListener el) {
	return add(state, entries, el);
    }

    public EntryAutomatListener reach(State state, EntryListener el) {
	return add(state, reaches, el);
    }

    public EntryAutomatListener exit(State state, EntryListener el) {
	return add(state, exits, el);
    }

    public EntryAutomatListener entry(State state, Runnable run) {
	return add(state, entries, run);
    }

    public EntryAutomatListener reach(State state, Runnable run) {
	return add(state, reaches, run);
    }

    public EntryAutomatListener exit(State state, Runnable run) {
	return add(state, exits, run);
    }

    public EntryAutomatListener entry(State state, Consumer<InstanceState> run) {
	return add(state, entries, run);
    }

    public EntryAutomatListener reach(State state, Consumer<InstanceState> run) {
	return add(state, reaches, run);
    }

    public EntryAutomatListener exit(State state, Consumer<InstanceState> run) {
	return add(state, exits, run);
    }

    private EntryAutomatListener add(State state, Map<State, List<EntryListener>> map, Consumer<InstanceState> consumer) {
	return add(state, map, (id, s, e) -> consumer.accept(id));
    }

    private EntryAutomatListener add(State state, Map<State, List<EntryListener>> map, Runnable run) {
	return add(state, map, (id, s, e) -> run.run());
    }

    private EntryAutomatListener add(State state, Map<State, List<EntryListener>> map, EntryListener el) {
	map.computeIfAbsent(state, s -> new ArrayList<>()).add(el);
	return this;
    }

    private void handle(Map<State, List<EntryListener>> map, InstanceState state, State reached, Event event) {
	List<EntryListener> ls = map.get(reached);
	if (ls != null)
	    ls.forEach(l -> l.handle(state, reached, event));
    }

    @Override
    public void reaching(InstanceState state, State reached, StateType type, Event event) {
	super.reaching(state, reached, type, event);
	handle(reaches, state, reached, event);
    }

    @Override
    public void entering(InstanceState state, State entered, Event event) {
	super.entering(state, entered, event);
	handle(entries, state, entered, event);
    }

    @Override
    public void leaving(InstanceState state, State leaved, Event event) {
	super.leaving(state, leaved, event);
	handle(exits, state, leaved, event);
    }

}
