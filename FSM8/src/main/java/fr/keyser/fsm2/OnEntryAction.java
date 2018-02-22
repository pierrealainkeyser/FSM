package fr.keyser.fsm2;

/**
 * Une action pour l'entrée dans un état
 * 
 * @author pakeyser
 *
 * @param <E>
 */
@FunctionalInterface
public interface OnEntryAction<E> {

    /**
     * Réalise l'action
     * 
     * @param event
     * @throws Exception
     */
    public void onEntry(Event<E> event) throws Exception;
}
