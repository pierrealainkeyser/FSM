package fr.keyser.fsm.hive;

public interface TerminateNotifier {
	/**
	 * Permet de créer un événement sur la fin du flu
	 * 
	 * @return
	 */
	public TargetedEvent sendOnTerminate();
}
