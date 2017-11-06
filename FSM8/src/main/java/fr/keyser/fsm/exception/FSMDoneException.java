package fr.keyser.fsm.exception;

import fr.keyser.fsm.FSMException;

public class FSMDoneException extends FSMException {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1276232627519562244L;

	public FSMDoneException() {
		super("FSM is done");
	}
}
