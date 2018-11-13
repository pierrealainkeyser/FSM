package fr.keyser.n.fsm;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

public class Event {

    public static class Builder {
	private final Map<String, Object> args = new HashMap<>();

	private final String key;

	private Builder(String key) {
	    this.key = key;
	}

	public <T extends Event> T build(BiFunction<String, Map<String, Object>, T> builder) {
	    return builder.apply(key, args);
	}

	public Event build() {
	    return build(Event::new);
	}

	public Builder id(InstanceId id) {
	    return put(INSTANCE_ID, id);
	}

	public Builder put(String key, Object value) {
	    args.put(key, value);
	    return this;
	}
    }

    public static final String INSTANCE_ID = "_id";

    public static Builder event(String key) {
	return new Builder(key);
    }

    private final Map<String, Object> args;

    private final String key;

    protected Event(String key) {
	this(key, Collections.emptyMap());
    }

    protected Event(String key, Map<String, Object> args) {
	this.key = key;
	this.args = Collections.unmodifiableMap(args);
    }

    public boolean containsKey(String key) {
	return args.containsKey(key);
    }

    public Set<Entry<String, Object>> entrySet() {
	return args.entrySet();
    }

    public Object get(String key) {
	return args.get(key);
    }

    public InstanceId getId() {
	Object object = get(INSTANCE_ID);
	if (object instanceof InstanceId)
	    return (InstanceId) object;
	else
	    return null;
    }

    public String getKey() {
	return key;
    }

    public Object getOrDefault(String key, Object defaultValue) {
	return args.getOrDefault(key, defaultValue);
    }

    public int size() {
	return args.size();
    }

    @Override
    public String toString() {
	String base = key;
	if (args.isEmpty())
	    return base;
	return base + " " + args;
    }

    public Collection<Object> values() {
	return args.values();
    }

}
