package fr.keyser.fsm;

/**
 * Une action lors de la sortie d'un état
 * 
 * @author pakeyser
 *
 * @param <C>
 */
@FunctionalInterface
public interface OnExitAction<S, E, C> {

	public final static class NamedExitAction<S, E, C> implements OnExitAction<S, E, C> {
		private final String name;

		private final OnExitAction<S, E, C> action;

		public NamedExitAction(String name, OnExitAction<S, E, C> action) {
			this.name = name;
			this.action = action;
		}

		@Override
		public void onExit(FSMState<S, C> context, FSMEvent<E> event) throws Exception {
			action.onExit(context, event);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static <S, E, C> OnExitAction<S, E, C> named(String name, OnExitAction<S, E, C> onTransitionAction) {
		return new NamedExitAction<>(name, onTransitionAction);
	}

	/**
	 * Action de sortie
	 * 
	 * @param context
	 * @param event
	 * @throws Exception
	 */
	public void onExit(FSMState<S, C> context, FSMEvent<E> event) throws Exception;
}
