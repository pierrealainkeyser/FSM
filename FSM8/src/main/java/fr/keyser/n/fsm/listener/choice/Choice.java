package fr.keyser.n.fsm.listener.choice;

import java.util.Collections;
import java.util.Map;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceState;

public class Choice extends Event {
    private static final String OTHERWISE = "otherwise";

    private static final String PROPS = "props";

    public Choice(String key, Map<String, Object> args) {
	super(key, args);

    }

    public Choice build(String index, boolean otherwise) {
	return Event.event(index)
	        .id(getId())
	        .put(OTHERWISE, otherwise)
	        .put(PROPS, getProps())
	        .build(Choice::new);

    }
    
    
    public final static Choice choice(String key,Event parent, boolean otherwise) {
	return Event.event(key)
	        .id(parent.getId())
	        .put(OTHERWISE, otherwise)
	        .build(Choice::new);
    }

    public final static Choice choice(InstanceState state) {
	return Event.event("<choice>")
	        .id(state.getId())
	        .put(PROPS, Collections.unmodifiableMap(state.getProps()))
	        .build(Choice::new);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getProps() {
	return (Map<String, Object>) get(PROPS);
    }

    public boolean isOtherwise() {
	return (Boolean) get(OTHERWISE);
    }

}
