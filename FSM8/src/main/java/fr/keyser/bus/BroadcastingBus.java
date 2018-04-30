package fr.keyser.bus;

import java.util.ArrayList;
import java.util.List;

public class BroadcastingBus implements Bus {

    private final List<Bus> delegated;

    public BroadcastingBus(List<? extends Bus> delegated) {
	this.delegated = new ArrayList<>(delegated);
    }

    @Override
    public void forward(Object event) {
	delegated.forEach(d -> d.forward(event));

    }

}
