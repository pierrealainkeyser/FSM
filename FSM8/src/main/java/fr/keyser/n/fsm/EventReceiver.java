package fr.keyser.n.fsm;

public interface EventReceiver {

    public void receive(Event evt);

    public default void receive(String evt) {
	receive(Event.event(evt));
    }

    public default void receive(Event.Builder builder) {
	receive(builder.build());
    }
}
