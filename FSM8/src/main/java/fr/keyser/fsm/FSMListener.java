package fr.keyser.fsm;

/**
 * Permet d'écouter les changements
 * 
 * @author pakeyser
 *
 * @param <S>
 *            le type des états
 * @param <E>
 *            le type des évènèments
 */
public interface FSMListener<S, E, C> {

	/**
	 * Une transition va être déclenchée
	 * 
	 * @param fsm
	 * @param event
	 * @param newState
	 */
	public void willTransit(FSMState<S, C> fsm, FSMEvent<E> event, S newState);

	/**
	 * L'automate est arrivé dans l'état
	 * 
	 * @param newState
	 */
	public void stateReached(FSMState<S, C> fsm);

	/**
	 * Une exception est lancée
	 * 
	 * @param fsm
	 * @param event
	 * @param exception
	 */
	public void exceptionThrowed(FSMState<S, C> fsm, FSMEvent<E> event, FSMException exception);
}
