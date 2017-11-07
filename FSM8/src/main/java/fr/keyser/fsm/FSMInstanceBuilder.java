package fr.keyser.fsm;

/**
 * Un outil qui permet de configurer les objets
 * 
 * @author pakeyser
 *
 * @param <S>
 * @param <E>
 * @param <C>
 */
public interface FSMInstanceBuilder<S, E, C> {

    /**
     * Rajoute un écouteur sur l'instance
     * 
     * @param listener
     * @return
     */
    public FSMInstanceBuilder<S, E, C> listener(FSMListener<S, E, C> listener);

    /**
     * Démarre une nouvelle instance. L'instance rentre dans l'état initial et
     * déclenche l'évenement d'entrée.
     * 
     * @param context
     *            le contexte de l'instance
     * @return l'instance
     */
    public FSMInstance<S, E, C> start(C context) throws FSMException;

    /**
     * Retourne à l'exécution, sans lancer aucun évènement
     * 
     * @param context
     *            le context
     * @param state
     *            l'état dans lequel l'instance va se trouver
     * @return la nouvelle instance
     */
    public FSMInstance<S, E, C> resume(FSMState<S, C> state) throws FSMException;
}
