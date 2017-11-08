package fr.keyser.fsm.exception;

import fr.keyser.fsm.FSMException;

public class FSMNoSuchTransition extends FSMException {

    /**
     * 
     */
    private static final long serialVersionUID = 8431784615541329576L;

    public FSMNoSuchTransition(String message) {
	super(message);
    }

}
