package fr.keyser.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import fr.keyser.fsm.SequentialExecutor;

public class Bus {

    private class Listener<T> {

	private final List<ListenerSuscription<T>> list = new ArrayList<>();

	private final Class<T> type;

	private Listener(Class<T> type) {
	    this.type = type;
	}

	public ListenerSuscription<T> add(Consumer<T> consumer) {
	    ListenerSuscription<T> s = new ListenerSuscription<T>(consumer, this);
	    list.add(s);
	    return s;
	}

	public boolean forward(T event) {
	    Iterator<ListenerSuscription<T>> it = list.iterator();
	    while (it.hasNext()) {
		boolean ok = it.next().forward(event);
		if (!ok)
		    it.remove();
	    }

	    return !list.isEmpty();
	}

	public void removeIfEmpty() {
	    if (list.isEmpty()) {
		listeners.remove(type);
	    }
	}

    }

    private static class ListenerSuscription<T> {

	private Consumer<T> consumer;

	private final Listener<T> parent;

	public ListenerSuscription(Consumer<T> consumer, Listener<T> parent) {
	    this.consumer = consumer;
	    this.parent = parent;
	}

	private void cancel(boolean removeIfEmpty) {
	    consumer = null;
	    if (removeIfEmpty)
		parent.removeIfEmpty();
	}

	public boolean forward(T event) {
	    if (consumer != null) {
		consumer.accept(event);
		return true;
	    } else
		return false;
	}
    }

    public class Suscription {

	private final List<ListenerSuscription<?>> suscriptions = new ArrayList<>();

	/**
	 * Removed on next run
	 */
	public void cancel() {
	    executor.execute(() -> doClear(false));
	}

	/**
	 * Removal
	 */
	public void clear() {
	    executor.execute(() -> doClear(true));
	}

	private void doClear(boolean clear) {
	    suscriptions.forEach(s -> s.cancel(clear));
	}

	@SuppressWarnings("unchecked")
	private <T> void listenTo(Class<T> type, Consumer<? extends T> consumer) {
	    Listener<T> listener = (Listener<T>) listeners.get(type);
	    if (listener == null)
		listeners.put(type, listener = new Listener<>(type));

	    ListenerSuscription<T> sub = listener.add((Consumer<T>) consumer);
	    suscriptions.add(sub);
	}
    }

    private final SequentialExecutor executor = new SequentialExecutor();

    private final Map<Class<?>, Listener<?>> listeners = new HashMap<>();

    public <T> Suscription listenTo(Class<T> type, Consumer<? extends T> consumer) {

	Suscription s = new Suscription();

	executor.execute(() -> s.listenTo(type, consumer));

	return s;

    }

    public void forward(Object event) {
	executor.execute(() -> {
	    Class<? extends Object> type = event.getClass();

	    // TODO all classes and interfaces

	    forwardEventTo(event, type);
	});
    }

    private void forwardEventTo(Object event, Class<?> type) {
	Listener<?> listener = listeners.get(type);
	if (listener != null) {
	    @SuppressWarnings("unchecked")
	    Listener<Object> l = (Listener<Object>) listener;
	    boolean ok = l.forward(event);
	    if (!ok)
		listeners.remove(type);
	}
    }

}
