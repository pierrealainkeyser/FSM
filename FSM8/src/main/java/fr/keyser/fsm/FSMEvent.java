package fr.keyser.fsm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FSMEvent<E> {

	private final E event;

	private final List<Object> args;

	public FSMEvent(E event, Object... args) {
		this(event, Arrays.asList(args));
	}

	public E getEvent() {
		return event;
	}

	public List<Object> getArgs() {
		return args;
	}

	public FSMEvent(E event, List<Object> args) {
		this.event = event;
		this.args = Collections.unmodifiableList(args);
	}
}
