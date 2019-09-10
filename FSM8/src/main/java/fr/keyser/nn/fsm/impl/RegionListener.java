package fr.keyser.nn.fsm.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.n.fsm.State;

public class RegionListener<T> extends DelegatedAutomatsListener<T> {

    private final Map<State, RegionConfiguration<T>> configurations;

    private final Set<State> joins;

    public RegionListener(AutomatsListener<T> listener, Collection<RegionConfiguration<T>> regions) {
	super(listener);
	this.configurations = regions.stream().collect(Collectors.toMap(RegionConfiguration::getRegion, Function.identity()));
	this.joins = regions.stream().flatMap(r -> {
	    if (r.isAutoJoin())
		return Stream.empty();
	    else
		return Stream.of(r.getJoinState());
	}).collect(Collectors.toSet());
    }

    @Override
    public boolean guard(Instance<T> instance, EventMsg event) {
	State state = instance.getState();
	if (configurations.containsKey(state) && !(event instanceof RegionEvent)) {
	    return false;
	}

	if (joins.contains(state)) {
	    return false;
	}

	return super.guard(instance, event);
    }

    @Override
    public Instance<T> entering(Instance<T> instance, State entered, EventMsg event) {

	if (event instanceof Merge) {
	    Merge merge = (Merge) event;
	    State region = merge.getRegion();

	    RegionConfiguration<T> conf = configurations.get(region);

	    return conf.merge(instance, merge.getPayload());

	}

	if (configurations.containsKey(entered)) {
	    startChilds(instance, event);
	    return instance;
	} else if (joins.contains(entered)) {
	    checkedJoined(instance);
	}

	return super.entering(instance, entered, event);
    }

    @Override
    public T start(Instance<T> parent, EventMsg event, int index) {

	State state = parent.getState();
	RegionConfiguration<T> config = configurations.get(state);
	if (config != null) {
	    return config.createChild(parent.get(Function.identity()), event, index);
	}

	return super.start(parent, event, index);
    }

    private void startChilds(Instance<T> instance, EventMsg event) {
	// start all non joining child
	RegionConfiguration<T> config = configurations.get(instance.getState());

	int count = 0;
	int times = config.getTimes();
	for (State child : config.getStates()) {
	    for (int i = 0; i < times; ++i) {
		instance.startChild(child, event, count++);
	    }
	}

	if (config.isAutoJoin()) {
	    doJoin(instance);
	}
    }

    private void checkedJoined(Instance<T> instance) {
	Instance<T> parent = instance.getParent();
	RegionConfiguration<T> config = configurations.get(instance.getState());
	List<Instance<T>> childs = parent.childsInstances();
	long joined = childs.stream().filter(i -> joins.contains(i.getState())).count();
	long expected = config.getTotal();
	if (joined == expected) {

	    // kill childs
	    for (Instance<T> child : childs)
		child.submit(PoisonPill.kill(child.getInstanceId()));

	    doJoin(parent);

	}
    }

    private void doJoin(Instance<T> parent) {
	// send joined
	parent.submit(Joined.join(parent.getInstanceId()));
    }

}
