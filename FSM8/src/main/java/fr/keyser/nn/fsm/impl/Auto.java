package fr.keyser.nn.fsm.impl;

import fr.keyser.n.fsm.InstanceId;

public class Auto extends EventMsg {

    public static final String KEY = "<auto>";

    private Auto(InstanceId target) {
	super(KEY, target, null);
    }

    public final static Auto auto(InstanceId id) {
	return new Auto(id);
    }

}
