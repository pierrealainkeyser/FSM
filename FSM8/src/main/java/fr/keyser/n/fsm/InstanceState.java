package fr.keyser.n.fsm;

import java.util.HashMap;
import java.util.Map;

public class InstanceState {

    private InstanceId id;

    private InstanceId parentId;

    private Map<String, Object> props = new HashMap<String, Object>();

    private State state;

    public InstanceState() {
    }

    public InstanceState(InstanceId id, InstanceId parentId, State state) {
	this.id = id;
	this.parentId = parentId;
	this.state = state;
    }

    public InstanceId getId() {
	return id;
    }

    public InstanceId getParentId() {
	return parentId;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public State getState() {
	return state;
    }

    public void setId(InstanceId id) {
        this.id = id;
    }

    public void setParentId(InstanceId parentId) {
        this.parentId = parentId;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
	return "InstanceState[state=" + state + ", id=" + id + ", parentId=" + parentId + "]";
    }
}
