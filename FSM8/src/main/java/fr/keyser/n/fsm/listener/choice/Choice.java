package fr.keyser.n.fsm.listener.choice;

import java.util.Map;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.State;

public class Choice extends Event {

    private static final String DESTINATION = "destination";
    private static final String OTHERWISE = "otherwise";

    private Choice(String key, Map<String, Object> args) {
	super(key, args);

    }

    public Choice build(String index, boolean otherwise) {
	return Event.event(index)
	        .id(getId())
	        .put(OTHERWISE, otherwise)
	        .build(Choice::new);

    }

    public final static Choice choice(InstanceId id) {
	return Event.event("<choice>")
	        .id(id)
	        .build(Choice::new);
    }

    public State getDestination() {
	return (State) get(DESTINATION);
    }

    public boolean isOtherwise() {
	return (Boolean) get(OTHERWISE);
    }

}
