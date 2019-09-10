package fr.keyser.nn.fsm.impl;

import fr.keyser.n.fsm.InstanceId;
import fr.keyser.n.fsm.State;

public class Merge extends EventMsg implements RegionEvent {

    private final State region;

    public final static Merge merge(InstanceId id, Object payload, State region) {
	return new Merge(id, payload, region);
    }

    private Merge(InstanceId target, Object payload, State region) {
	super("<merge>", target, payload);
	this.region = region;
    }

    public State getRegion() {
        return region;
    }

}
