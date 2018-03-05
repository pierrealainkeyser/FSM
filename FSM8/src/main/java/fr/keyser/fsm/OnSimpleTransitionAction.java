package fr.keyser.fsm;

/**
 * Action produite durant une transition
 * 
 * @author pakeyser
 *
 * @param <E>
 */
@FunctionalInterface
public interface OnSimpleTransitionAction<E> {

    /**
     * Le comportement lors de la transition
     * 
     * @param event
     * @throws Exception
     */
    public void onTransition(Event<E> event) throws Exception;
}
