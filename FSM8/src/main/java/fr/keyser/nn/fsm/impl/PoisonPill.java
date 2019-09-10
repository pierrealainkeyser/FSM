package fr.keyser.nn.fsm.impl;

import fr.keyser.n.fsm.InstanceId;

public class PoisonPill extends EventMsg {

    private PoisonPill(InstanceId target) {
	super("<poison>", target, null);
    }

    public final static PoisonPill kill(InstanceId id) {
	return new PoisonPill(id);
    }

}
