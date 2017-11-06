package fr.keyser.fsm.exception;

import fr.keyser.fsm.FSMException;

public class FSMNullEventException extends FSMException {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1276232627519562244L;

	public FSMNullEventException() {
		super("The event parameter is mandatory");
	}
}
