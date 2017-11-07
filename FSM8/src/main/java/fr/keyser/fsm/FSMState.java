package fr.keyser.fsm;

/**
 * L'état d'une exécution d'un {@link FSM}
 * 
 * @author pakeyser
 *
 * @param <S>
 * @param <C>
 */
public interface FSMState<S, C> {

    /**
     * Renvoi la clé unique de l'instance associé
     * 
     * @return
     */
    public FSMInstanceKey getKey();

    /**
     * L'état courant
     * 
     * @return l'état courant
     */
    public S getCurrentState();

    /**
     * Accède au contexte
     * 
     * @return le contexte
     */
    public C getContext();

    /**
     * Renvoi vrai si l'exécution du {@link FSM} est terminée
     * 
     * @return vrai si terminée;
     */
    public boolean isDone();

    /**
     * Le compteur de transition passé
     * 
     * @return
     */
    public long getTransitionCount();

}
