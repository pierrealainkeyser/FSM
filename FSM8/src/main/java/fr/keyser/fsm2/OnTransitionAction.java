package fr.keyser.fsm2;

/**
 * Action produite durant une transition
 * 
 * @author pakeyser
 *
 * @param <S>
 * @param <E>
 */
@FunctionalInterface
public interface OnTransitionAction<S, E> {

    /**
     * Le comportement lors de la transition
     * 
     * @param from
     * @param event
     * @parma to
     * @throws Exception
     */
    public void onTransition(State<S> from, Event<E> event, State<S> to) throws Exception;
}
