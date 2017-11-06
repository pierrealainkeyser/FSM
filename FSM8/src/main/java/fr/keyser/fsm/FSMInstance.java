package fr.keyser.fsm;

/**
 * Le point d'accès à une instance d'exécution d'un {@link FSM}
 * 
 * @author pakeyser
 *
 * @param <E>
 *            le type d'évènement
 * @param <C>
 *            le type de contexte
 */
public interface FSMInstance<S, E, C> extends FSMState<S, C> {

	/**
	 * Envoi un évènement à l'instance
	 * 
	 * @param event
	 *            l'évènement à transmettre
	 * @throws FSMException
	 *             en cas d'erreur
	 */
	public void sendEvent(E event, Object... args) throws FSMException;
}
