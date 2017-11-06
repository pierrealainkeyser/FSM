package fr.keyser.fsm.exception;

import fr.keyser.fsm.FSMException;

public class FSMNoSuchStateException extends FSMException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2045608825205979492L;

	public FSMNoSuchStateException(String message) {
		super(message);
	}

}
