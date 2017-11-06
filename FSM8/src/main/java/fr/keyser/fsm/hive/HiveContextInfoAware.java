package fr.keyser.fsm.hive;

/**
 * Une interface de marquage pour savoir que le contexte est disponible
 * 
 * @author pakeyser
 *
 */
public interface HiveContextInfoAware {

	/**
	 * Les informations du contexte
	 * 
	 * @return
	 */
	public HiveContextInfo getHiveContextInfo();

}
