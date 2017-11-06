package fr.keyser.fsm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FSMEvent<E> {

    private final List<Object> args;

    private final E event;

    public FSMEvent(E event, List<Object> args) {
	this.event = event;
	this.args = Collections.unmodifiableList(args);
    }

    public FSMEvent(E event, Object... args) {
	this(event, Arrays.asList(args));
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	@SuppressWarnings("rawtypes")
	FSMEvent other = (FSMEvent) obj;
	if (args == null) {
	    if (other.args != null)
		return false;
	} else if (!args.equals(other.args))
	    return false;
	if (event == null) {
	    if (other.event != null)
		return false;
	} else if (!event.equals(other.event))
	    return false;
	return true;
    }

    public List<Object> getArgs() {
	return args;
    }

    public E getEvent() {
	return event;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((args == null) ? 0 : args.hashCode());
	result = prime * result + ((event == null) ? 0 : event.hashCode());
	return result;
    }
}
