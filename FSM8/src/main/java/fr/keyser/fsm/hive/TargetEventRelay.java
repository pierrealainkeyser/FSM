package fr.keyser.fsm.hive;

/**
 * Permet de faire suivre des {@link TargetedEvent}
 * 
 * @author pakeyser
 *
 */
public interface TargetEventRelay {

	/**
	 * Permet d'envoyer un message
	 * 
	 * @param event
	 * @param success
	 */
	public void sendEvent(TargetedEvent event, boolean success);
}
