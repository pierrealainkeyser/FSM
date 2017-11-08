package fr.keyser.fsm;

/**
 * Permet de visiter un FSM
 * 
 * @author pakeyser
 *
 * @param <S>
 *            le type des états
 * @param <E>
 *            le type des évènements
 */
public interface FSMVisitor<S, E> {

    /**
     * Visite l'état initial
     * 
     * @param initial
     * @param enter
     * @param exit
     */
    public void initial(S initial, OnEnterAction<S, E, ?> enter, OnExitAction<S, E, ?> exit);

    /**
     * Visite un état
     * 
     * @param state
     * @param enter
     * @param exit
     * @param terminal
     */
    public void state(S state, OnEnterAction<S, E, ?> enter, OnExitAction<S, E, ?> exit, boolean terminal);

    /**
     * Visite une transition
     * 
     * @param from
     * @param event
     * @param action
     * @param destination
     */
    public void transition(S from, E event, OnTransitionAction<S, E, ?> action, S destination);

}
