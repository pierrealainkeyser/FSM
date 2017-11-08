package fr.keyser.fsm;

public abstract class FSMException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -2712192308287379509L;

    public FSMException(String message) {
	super(message);
    }

    public FSMException(Throwable cause) {
	super(cause);
    }

}
