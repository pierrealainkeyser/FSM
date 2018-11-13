package fr.keyser.n.fsm.automat;

import java.util.function.Supplier;

import fr.keyser.n.fsm.InstanceId;

public class SequenceInstanceIdSupplier implements Supplier<InstanceId> {

    private long id = 0;

    @Override
    public InstanceId get() {
	return new InstanceId(Long.toString(id++));
    }

}
