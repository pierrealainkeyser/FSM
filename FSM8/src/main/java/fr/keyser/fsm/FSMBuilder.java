package fr.keyser.fsm;

import static java.util.Collections.unmodifiableMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Permet de créer des {@link FSM} avec une interface fluent
 * 
 * @author pakeyser
 *
 * @param <S>
 *            le type des états
 * @param <E>
 *            le type des évènements
 * @param <C>
 *            le context
 */
public class FSMBuilder<S, E, C> {

	/**
	 * Permet de construire des {@link State} avec des {@link Transition} et des
	 * actions
	 * 
	 * @author pakeyser
	 *
	 */
	public class StateBuilder {

		private OnEnterAction<S, E, C> onEnter;

		private OnExitAction<S, E, C> onExit;

		private final S state;

		private boolean terminal;

		private final Map<E, Transition<S, E, C>> transitions = new LinkedHashMap<>();

		StateBuilder(S state) {
			this.state = state;
		}

		/**
		 * Renvoi le {@link FSMBuilder}
		 * 
		 * @return
		 */
		public FSMBuilder<S, E, C> and() {
			return FSMBuilder.this;
		}

		/**
		 * Appel la méthode {@link FSMBuilder#build()}
		 * 
		 * @return
		 */
		public FSM<S, E, C> build() {
			return and().build();
		}

		/**
		 * Rajoute une action sur l'entrée dans un état
		 * 
		 * @param onEnter
		 * @return
		 */
		public StateBuilder onEnter(OnEnterAction<S, E, C> onEnter) {
			this.onEnter = onEnter;
			return this;
		}

		/**
		 * Rajoute une action sur la sortie de l'état
		 * 
		 * @param onExit
		 * @return
		 */
		public StateBuilder onExit(OnExitAction<S, E, C> onExit) {
			this.onExit = onExit;
			return this;
		}

		/**
		 * Marque l'état comme terminal
		 * 
		 * @return
		 */
		public StateBuilder terminal() {
			this.terminal = true;
			return this;
		}

		State<S, E, C> toState() {
			return new State<S, E, C>(state, onEnter, onExit, unmodifiableMap(new LinkedHashMap<>(transitions)),
					terminal);
		}

		/**
		 * Rajoute une transition sans action
		 * 
		 * @param event
		 * @param destination
		 * @return
		 */
		public StateBuilder transition(E event, S destination) {
			return transition(event, destination, null);
		}

		/**
		 * Rajoute une transition avec une action
		 * 
		 * @param event
		 * @param destination
		 * @param onTransition
		 * @return
		 */
		public StateBuilder transition(E event, S destination, OnTransitionAction<S, E, C> onTransition) {
			Transition<S, E, C> transition = new Transition<S, E, C>(destination, event, onTransition);
			transitions.put(event, transition);
			return this;
		}
	}

	private S initial;

	private final Map<S, StateBuilder> states = new LinkedHashMap<S, StateBuilder>();

	private List<FSMListener<S, E, C>> listeners;

	/**
	 * Création du {@link FSM}
	 * 
	 * @return le {@link FSM} correctement configuré
	 */
	public FSM<S, E, C> build() {
		List<State<S, E, C>> states = this.states.values().stream().map(StateBuilder::toState)
				.collect(Collectors.toList());
		return new FSMEngine<S, E, C>(initial, states, listeners);

	}

	/**
	 * Rajoute un écouteur
	 * 
	 * @param listener
	 * @return
	 */
	public FSMBuilder<S, E, C> listener(FSMListener<S, E, C> listener) {
		if (listeners == null)
			listeners = new ArrayList<>();
		listeners.add(listener);
		return this;
	}

	/**
	 * Rajout un etat ou accède à un état déjà produit. Le premier état est l'état
	 * initial
	 * 
	 * @param state
	 * @return
	 */
	public StateBuilder state(S state) {

		if (states.containsKey(state))
			return states.get(state);

		if (initial == null)
			initial = state;

		StateBuilder builder = new StateBuilder(state);
		states.put(state, builder);
		return builder;
	}

}
