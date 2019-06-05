package fr.keyser.n.fsm;

import java.util.Map;

public class InstanceState {

    private InstanceId id;

    private InstanceId parentId;

    private Map<String, Object> props;

    private State state;

    public InstanceState() {
    }

    public InstanceState(InstanceId id, InstanceId parentId, State state, Map<String, Object> props) {
	this.id = id;
	this.parentId = parentId;
	this.state = state;
	this.props = props;
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

    @Override
    public String toString() {
	return "InstanceState[state=" + state + ", id=" + id + ", parentId=" + parentId +"]";
    }
}
