package fr.keyser.nn.fsm.impl;

import java.util.ArrayList;
import java.util.List;

import fr.keyser.n.fsm.InstanceId;

public class EventMsg {

    private final String key;

    private final Object payload;

    private final InstanceId target;

    public static EventMsg unicast(String key, InstanceId target, Object payload) {
	return new EventMsg(key, target, payload);
    }

    public static EventMsg unicast(String key, InstanceId target) {
	return unicast(key, target, null);
    }

    public static EventMsg broadcast(String key, Object payload) {
	return new EventMsg(key, null, payload);
    }

    public static EventMsg broadcast(String key) {
	return broadcast(key, null);
    }

    public EventMsg(String key, InstanceId target, Object payload) {
	this.key = key;
	this.target = target;
	this.payload = payload;
    }

    public String getKey() {
	return key;
    }

    public Object getPayload() {
	return payload;
    }

    public InstanceId getTarget() {
	return target;
    }

    @Override
    public String toString() {
	List<String> str = new ArrayList<>();
	if (key != null)
	    str.add("key=" + key);
	if (target != null)
	    str.add("target=" + target);
	if (payload != null)
	    str.add("payload");

	String name = getClass().getSimpleName();
	return name  +str;
    }

}
