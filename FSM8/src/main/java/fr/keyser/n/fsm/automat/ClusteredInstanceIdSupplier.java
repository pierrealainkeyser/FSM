package fr.keyser.n.fsm.automat;

import java.util.UUID;
import java.util.function.Supplier;

import fr.keyser.n.fsm.InstanceId;

public class ClusteredInstanceIdSupplier implements Supplier<InstanceId> {

    public final static ClusteredInstanceIdSupplier INSTANCE = new ClusteredInstanceIdSupplier();

    @Override
    public InstanceId get() {
	String uuid = UUID.randomUUID().toString();
	return new InstanceId(uuid);
    }

}
