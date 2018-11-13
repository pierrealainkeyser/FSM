package fr.keyser.n.fsm;

public class InstanceState {

    private State state;

    private InstanceId id;

    private InstanceId parentId;

    public InstanceState() {
    }

    public InstanceState(InstanceId id, InstanceId parentId, State state) {
	this.id = id;
	this.parentId = parentId;
	this.state = state;
    }

    public State getState() {
	return state;
    }

    public InstanceId getId() {
	return id;
    }

    public InstanceId getParentId() {
	return parentId;
    }

    public void setState(State current) {
	this.state = current;
    }

    public void setId(InstanceId id) {
	this.id = id;
    }

    public void setParentId(InstanceId parentId) {
	this.parentId = parentId;
    }

    @Override
    public String toString() {
	return "InstanceState[state=" + state + ", id=" + id + ", parentId=" + parentId + "]";
    }

}
