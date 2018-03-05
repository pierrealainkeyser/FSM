package fr.keyser.fsm;

/**
 * Une action lors de la sortie d'un état
 * 
 * @author pakeyser
 *
 * @param <E>
 */
@FunctionalInterface
public interface OnExitAction<E> {

    /**
     * Action de sortie
     * 
     * @param event
     * @throws Exception
     */
    public void onExit(Event<E> event) throws Exception;
}
