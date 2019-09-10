package fr.keyser.nn.fsm.impl;

import fr.keyser.n.fsm.InstanceId;

public class Choice extends EventMsg {

    private final boolean otherwise;

    private Choice(String key, InstanceId target, boolean otherwise) {
	super(key, target, null);
	this.otherwise = otherwise;
    }

    public static Choice choice(InstanceId target) {
	return new Choice("<choice>", target, false);
    }

    public Choice build(String index, boolean otherwise) {
	return new Choice(index, getTarget(), otherwise);

    }

    public boolean isOtherwise() {
	return otherwise;
    }

}
