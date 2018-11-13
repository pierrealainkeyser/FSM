package fr.keyser.n.fsm;

public class LazyEventReceiver implements EventReceiver {

    private EventReceiver eventReceiver;

    @Override
    public void receive(Event evt) {
	if (eventReceiver == null)
	    throw new IllegalStateException("Receiver is not set");
	eventReceiver.receive(evt);
    }

    public void setEventReceiver(EventReceiver eventReceiver) {
	this.eventReceiver = eventReceiver;
    }
}
