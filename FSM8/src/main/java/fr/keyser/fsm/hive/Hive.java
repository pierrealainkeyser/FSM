package fr.keyser.fsm.hive;

import fr.keyser.fsm.FSM;
import fr.keyser.fsm.FSMException;
import fr.keyser.fsm.FSMInstance;
import fr.keyser.fsm.FSMState;

/**
 * Permet de contenir et d'exécuter plusieurs {@link FSMInstance} de {@link FSM}
 * différent
 * 
 * @author pakeyser
 *
 */
public interface Hive {

	/**
	 * Renvoi l'instance
	 * 
	 * @param key
	 * @return
	 */
	public <S, E, C extends HiveContextInfoAware> FSMInstance<S, E, C> getInstance(FSMInstanceKey key);

	/**
	 * Démarre une nouvelle instance. L'instance rentre dans l'état initial et
	 * déclenche l'évenement d'entrée.
	 * 
	 * @param context
	 *            le contexte de l'instance. La méthode
	 *            {@link HiveContextInfoAware#getHiveContextInfo()) sera utilisé
	 * pour valoriser des informations comme
	 * HiveContextInfo#setInstanceKey(FSMInstanceKey)
	 * @return l'instance
	 */
	public <S, E, C extends HiveContextInfoAware> FSMInstance<S, E, C> start(FSMKey key, C context) throws FSMException;

	/**
	 * Retourne à l'exécution, sans lancer aucun évènement
	 * 
	 * @param key
	 *            la clé de l'instance
	 * @param state
	 *            l'état dans lequel l'instance va se trouver
	 * @return l'instance
	 */
	public <S, E, C extends HiveContextInfoAware> FSMInstance<S, E, C> resume(FSMInstanceKey key, FSMState<S, C> state)
			throws FSMException;
}
