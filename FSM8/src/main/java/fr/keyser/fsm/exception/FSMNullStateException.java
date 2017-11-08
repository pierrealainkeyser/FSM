package fr.keyser.fsm.exception;

import fr.keyser.fsm.FSMException;

public class FSMNullStateException extends FSMException {
    /**
    	 * 
    	 */
    private static final long serialVersionUID = 1276232627519562244L;

    public FSMNullStateException() {
	super("The state parameter is mandatory");
    }
}
