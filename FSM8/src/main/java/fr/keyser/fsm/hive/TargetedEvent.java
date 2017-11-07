package fr.keyser.fsm.hive;

import fr.keyser.fsm.FSMEvent;

/**
 * Un {@link FSMEvent} à destination d'un {@link FSMInstanceKey}
 * 
 * @author pakeyser
 *
 */
public class TargetedEvent {

    private final FSMInstanceKey instance;

    private final FSMEvent<?> event;

    public TargetedEvent(FSMInstanceKey instance, FSMEvent<?> event) {
	this.instance = instance;
	this.event = event;
    }

    public FSMInstanceKey getInstance() {
	return instance;
    }

    public FSMEvent<?> getEvent() {
	return event;
    }
}
