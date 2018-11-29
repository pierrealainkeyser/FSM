package fr.keyser.n.fsm;

public interface EventReceiver {

    public EventProcessingStatus receive(Event evt);

    public default EventProcessingStatus receive(String evt) {
	return receive(Event.event(evt));
    }

    public default EventProcessingStatus receive(Event.Builder builder) {
	return receive(builder.build());
    }
}
