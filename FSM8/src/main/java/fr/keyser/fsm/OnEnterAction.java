package fr.keyser.fsm;

/**
 * Une action pour l'entrée dans un état
 * 
 * @author pakeyser
 *
 * @param <S>
 * @param <E>
 * @param <C>
 */
@FunctionalInterface
public interface OnEnterAction<S, E, C> {

	public final static class NamedEnterAction<S, E, C> implements OnEnterAction<S, E, C> {
		private final String name;

		private final OnEnterAction<S, E, C> action;

		public NamedEnterAction(String name, OnEnterAction<S, E, C> action) {
			this.name = name;
			this.action = action;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public void onEnter(FSMInstance<S, E, C> instance, FSMEvent<E> event) throws Exception {
			action.onEnter(instance, event);
			
		}
	}

	public static <S, E, C> NamedEnterAction<S, E, C> named(String name, OnEnterAction<S, E, C> action) {
		return new NamedEnterAction<>(name, action);
	}

	/**
	 * Réalise l'action lorsque la {@link FSMInstance} rentre dans l'état
	 * 
	 * @param instance
	 * @throws Exception
	 */
	public void onEnter(FSMInstance<S, E, C> instance, FSMEvent<E> event) throws Exception;
}
