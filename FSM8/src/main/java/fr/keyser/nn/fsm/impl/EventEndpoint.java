package fr.keyser.nn.fsm.impl;

public interface EventEndpoint {
    
    public void submit(int priority, EventMsg event);

    public default void submit(EventMsg event) {
	submit(0,event);
    }
}
