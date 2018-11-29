package fr.keyser.n.fsm;

public class LazyEventReceiver implements EventReceiver {

    private EventReceiver eventReceiver;

    @Override
    public EventProcessingStatus receive(Event evt) {
	if (eventReceiver == null)
	    throw new IllegalStateException("Receiver is not set");
	return eventReceiver.receive(evt);
    }

    public void setEventReceiver(EventReceiver eventReceiver) {
	this.eventReceiver = eventReceiver;
    }
}
