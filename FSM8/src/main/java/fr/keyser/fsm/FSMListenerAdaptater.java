package fr.keyser.fsm;

/**
 * Un adapteur pour faciliter l'implémentation de {@link FSMListener}
 * 
 * @author pakeyser
 *
 * @param <S>
 * @param <E>
 * @param <C>
 */
public class FSMListenerAdaptater<S, E, C> implements FSMListener<S, E, C> {

    @Override
    public void willTransit(FSMState<S, C> fsm, FSMEvent<E> event, S newState) {

    }

    @Override
    public void stateReached(FSMState<S, C> fsm) {

    }

    @Override
    public void exceptionThrowed(FSMState<S, C> fsm, FSMEvent<E> event, FSMException exception) {

    }

}
