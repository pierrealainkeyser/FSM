package fr.keyser.fsm;

/**
 * Permet de n'afficher que le {@link FSMState} d'une {@link FSMInstance}
 * 
 * @author pakeyser
 *
 * @param <S>
 * @param <C>
 */
public class FSMStateWrapper<S, C> implements FSMState<S, C> {

    private final FSMInstance<S, ?, C> instance;

    public FSMStateWrapper(FSMInstance<S, ?, C> instance) {
	this.instance = instance;
    }

    @Override
    public C getContext() {
	return instance.getContext();
    }

    @Override
    public S getCurrentState() {
	return instance.getCurrentState();
    }

    @Override
    public long getTransitionCount() {
	return instance.getTransitionCount();
    }

    @Override
    public boolean isDone() {
	return instance.isDone();
    }

    @Override
    public FSMInstanceKey getKey() {
	return instance.getKey();
    }

}
