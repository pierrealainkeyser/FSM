package fr.keyser.fsm.hive;

/**
 * L'information d'une exécution
 * 
 * @author pakeyser
 *
 */
public class HiveContextInfo {

	private FSMInstanceKey instanceKey;

	public FSMInstanceKey getInstanceKey() {
		return instanceKey;
	}

	public void setInstanceKey(FSMInstanceKey instanceKey) {
		this.instanceKey = instanceKey;
	}
}
