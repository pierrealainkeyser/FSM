package fr.keyser.fsm;

class Transition<S, E, C> {
	private final S destination;

	private final E event;

	private final OnTransitionAction<S, E, C> onTransition;

	Transition(S destination, E event, OnTransitionAction<S, E, C> onTransition) {
		this.destination = destination;
		this.event = event;
		this.onTransition = onTransition;
	}

	public S getDestination() {
		return destination;
	}

	public E getEvent() {
		return event;
	}

	public OnTransitionAction<S, E, C> getOnTransition() {
		return onTransition;
	}
}
