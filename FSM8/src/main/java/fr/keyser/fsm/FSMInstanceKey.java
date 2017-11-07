package fr.keyser.fsm;

/**
 * Permet d'identifier une instance de {@link FSM}
 * 
 * @author pakeyser
 *
 */
public class FSMInstanceKey {

    private final FSMKey fsm;

    private final String instance;

    public FSMInstanceKey(FSMKey fsm, String instance) {
	this.fsm = fsm;
	this.instance = instance;
    }

    @Override
    public String toString() {
	return "FSMInstanceKey [fsm=" + fsm + ", instance=" + instance + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((fsm == null) ? 0 : fsm.hashCode());
	result = prime * result + ((instance == null) ? 0 : instance.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	FSMInstanceKey other = (FSMInstanceKey) obj;
	if (fsm == null) {
	    if (other.fsm != null)
		return false;
	} else if (!fsm.equals(other.fsm))
	    return false;
	if (instance == null) {
	    if (other.instance != null)
		return false;
	} else if (!instance.equals(other.instance))
	    return false;
	return true;
    }

    public String getInstance() {
	return instance;
    }

    public FSMKey getFsm() {
	return fsm;
    }
}
