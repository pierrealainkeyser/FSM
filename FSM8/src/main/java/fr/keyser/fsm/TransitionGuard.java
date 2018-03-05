package fr.keyser.fsm;

/**
 * Action produite durant une transition
 * 
 * @author pakeyser
 *
 * @param <E>
 */
@FunctionalInterface
public interface TransitionGuard<E> {

    /**
     * Le comportement lors de la transition
     * 
     * @param event
     */
    public boolean validate(Event<E> event) throws Exception;
}
