package fr.keyser.fsm;

/**
 * Action produite durant une transition
 * 
 * @author pakeyser
 *
 * @param <C>
 */
@FunctionalInterface
public interface OnTransitionAction<S, E, C> {

    public final static class NamedTransitionAction<S, E, C> implements OnTransitionAction<S, E, C> {
	private final String name;

	private final OnTransitionAction<S, E, C> action;

	public NamedTransitionAction(String name, OnTransitionAction<S, E, C> action) {
	    this.name = name;
	    this.action = action;
	}

	@Override
	public void onTransition(FSMInstance<S, E, C> context, FSMEvent<E> event) throws Exception {
	    action.onTransition(context, event);
	}

	@Override
	public String toString() {
	    return name;
	}
    }

    public static <S, E, C> OnTransitionAction<S, E, C> named(String name, OnTransitionAction<S, E, C> action) {
	return new NamedTransitionAction<>(name, action);
    }

    /**
     * Le comportement lors de la transition
     * 
     * @param context
     * @param event
     * @throws Exception
     */
    public void onTransition(FSMInstance<S, E, C> context, FSMEvent<E> event) throws Exception;
}
