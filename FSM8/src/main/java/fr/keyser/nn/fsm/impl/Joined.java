package fr.keyser.nn.fsm.impl;

import fr.keyser.n.fsm.InstanceId;

public class Joined extends EventMsg implements RegionEvent{

    public static final String KEY = "<joined>";

    private Joined(InstanceId target) {
	super(KEY, target, null);
    }

    public final static Joined join(InstanceId id) {
	return new Joined(id);
    }

}
