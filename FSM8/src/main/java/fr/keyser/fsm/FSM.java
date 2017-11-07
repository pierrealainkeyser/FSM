package fr.keyser.fsm;

/**
 * Un automate qui suit le patron "Poids mouche", il permet de démarrer des
 * {@link FSMInstance}.
 * 
 * @author pakeyser
 *
 * @param <S>
 *            le type d'état
 * @param <E>
 *            le type d'évènement
 * @param <C>
 *            le type du contexte
 */
public interface FSM<S, E, C> {

    /**
     * Permet de visiter l'automate
     * 
     * @param visitor
     *            le visiteur
     */
    public void visit(FSMVisitor<S, E> visitor);

    /**
     * Création du builder
     * 
     * @return
     */
    public FSMInstanceBuilder<S, E, C> instance();

    /**
     * Démarre une nouvelle instance. L'instance rentre dans l'état initial et
     * déclenche l'évenement d'entrée.
     * 
     * @param context
     *            le contexte de l'instance
     * @return l'instance
     */
    public default FSMInstance<S, E, C> start(C context) throws FSMException {
	return instance().start(context);
    }

    /**
     * Retourne à l'exécution, sans lancer aucun évènement
     * 
     * @param context
     *            le context
     * @param state
     *            l'état dans lequel l'instance va se trouver
     * @return la nouvelle instance
     */
    public default FSMInstance<S, E, C> resume(FSMState<S, C> state) throws FSMException {
	return instance().resume(state);
    }
}
