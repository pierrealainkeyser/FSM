package fr.keyser.n.fsm.listener.choice;

import java.util.HashMap;
import java.util.Map;

import fr.keyser.n.fsm.Event;
import fr.keyser.n.fsm.EventReceiver;
import fr.keyser.n.fsm.InstanceState;
import fr.keyser.n.fsm.State;
import fr.keyser.n.fsm.StateType;
import fr.keyser.n.fsm.Transition;
import fr.keyser.n.fsm.listener.AutomatListener;
import fr.keyser.n.fsm.listener.DelegatedAutomatListener;

public class ChoiceAutomatListener extends DelegatedAutomatListener {

    private final EventReceiver receiver;

    private final Map<State, Choices> choices = new HashMap<>();

    public ChoiceAutomatListener(EventReceiver receiver, AutomatListener listener) {
	super(listener);
	this.receiver = receiver;
    }

    public Choices choices(State state) {
	return choices.computeIfAbsent(state, s -> new Choices());
    }

    @Override
    public boolean guard(InstanceState state, Transition transition) {
	Event event = transition.getEvent();
	if (event instanceof Choice) {
	    Choice choice = (Choice) event;
	    if (choice.isOtherwise())
		return true;

	    Choices choices = this.choices.get(transition.getSource());
	    if (choices != null) {
		return choices.test(choice);
	    } else
		return false;
	}

	return super.guard(state, transition);
    }

    @Override
    public void reaching(InstanceState state, State reached, StateType type) {
	super.reaching(state, reached, type);
	if (StateType.CHOICE == type) {
	    receiver.receive(Choice.choice(state.getId()));
	}
    }
}
