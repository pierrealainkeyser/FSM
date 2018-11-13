package fr.keyser.n.fsm.listener.frontier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.listener.AutomatListener;
import fr.keyser.n.fsm.listener.DelegatedAutomatListener;

public class EntryAutomatListener extends DelegatedAutomatListener {

    private final Map<State, List<EntryListener>> entries = new HashMap<>();

    private final Map<State, List<EntryListener>> exits = new HashMap<>();

    public EntryAutomatListener() {

    }

    public EntryAutomatListener(AutomatListener listener) {
	super(listener);
    }

    public EntryAutomatListener entry(State state, EntryListener el) {
	return add(state, entries, el);
    }

    public EntryAutomatListener exit(State state, EntryListener el) {
	return add(state, exits, el);
    }

    public EntryAutomatListener entry(State state, Runnable run) {
	return add(state, entries, run);
    }

    public EntryAutomatListener exit(State state, Runnable run) {
	return add(state, exits, run);
    }

    public EntryAutomatListener entry(State state, Consumer<InstanceId> run) {
	return add(state, entries, run);
    }

    public EntryAutomatListener exit(State state, Consumer<InstanceId> run) {
	return add(state, exits, run);
    }

    private EntryAutomatListener add(State state, Map<State, List<EntryListener>> map, Consumer<InstanceId> consumer) {
	return add(state, map, (id, s, e) -> consumer.accept(id));
    }

    private EntryAutomatListener add(State state, Map<State, List<EntryListener>> map, Runnable run) {
	return add(state, map, (id, s, e) -> run.run());
    }

    private EntryAutomatListener add(State state, Map<State, List<EntryListener>> map, EntryListener el) {
	map.computeIfAbsent(state, s -> new ArrayList<>()).add(el);
	return this;
    }

    @Override
    public void entering(InstanceId id, State entered, Event event) {
	super.entering(id, entered, event);

	List<EntryListener> ls = entries.get(entered);
	if (ls != null)
	    ls.forEach(l -> l.handle(id, entered, event));
    }

    @Override
    public void leaving(InstanceId id, State leaved, Event event) {
	super.leaving(id, leaved, event);

	List<EntryListener> ls = exits.get(leaved);
	if (ls != null)
	    ls.forEach(l -> l.handle(id, leaved, event));
    }

}
