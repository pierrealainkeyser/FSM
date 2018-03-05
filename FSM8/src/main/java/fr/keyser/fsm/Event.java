package fr.keyser.fsm;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Event<E> implements Map<String, Object> {

    public static class Builder<E> {
	private Map<String, Object> args = new HashMap<>();

	private final E value;

	private Builder(E value) {
	    this.value = value;
	}

	public Event<E> build() {
	    return new Event<E>(value, args);
	}

	public Builder<E> put(String key, Object value) {
	    args.put(key, value);
	    return this;
	}
    }

    public final static <E> Builder<E> create(E value) {
	return new Builder<>(value);
    }

    private final Map<String, Object> args;

    private final E value;

    private Event(E value, Map<String, Object> args) {
	this.value = value;
	this.args = Collections.unmodifiableMap(args);
    }

    @Override
    public void clear() {
	args.clear();
    }

    @Override
    public boolean containsKey(Object key) {
	return args.containsKey(key);
    }

    public boolean containsValue(Object value) {
	return args.containsValue(value);
    }

    public Set<Entry<String, Object>> entrySet() {
	return args.entrySet();
    }

    public Object get(Object key) {
	return args.get(key);
    }

    public E getValue() {
	return value;
    }

    public boolean isEmpty() {
	return args.isEmpty();
    }

    public Set<String> keySet() {
	return args.keySet();
    }

    public Object put(String key, Object value) {
	return args.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
	args.putAll(m);

    }

    public Object remove(Object key) {
	return args.remove(key);
    }

    public int size() {
	return args.size();
    }

    public Collection<Object> values() {
	return args.values();
    }

}
