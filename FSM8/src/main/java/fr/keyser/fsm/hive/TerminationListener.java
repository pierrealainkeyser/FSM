package fr.keyser.fsm.hive;

import fr.keyser.fsm.FSMEvent;
import fr.keyser.fsm.FSMException;
import fr.keyser.fsm.FSMInstance;
import fr.keyser.fsm.FSMListener;
import fr.keyser.fsm.FSMState;

/**
 * Permet de suivre un {@link FSMInstance} qui se finit, et dont le contexte est
 * un {@link TerminateNotifier}
 * 
 * @author pakeyser
 *
 */
public class TerminationListener implements FSMListener<Object, Object, Object> {

	private final TargetEventRelay relay;

	public TerminationListener(TargetEventRelay relay) {
		this.relay = relay;
	}

	@Override
	public void willTransit(FSMState<Object, Object> fsm, FSMEvent<Object> event, Object newState) {

	}

	@Override
	public void stateReached(FSMState<Object, Object> fsm) {
		if (fsm.isDone())
			onTerminate(fsm, true);
	}

	private void onTerminate(FSMState<Object, Object> fsm, boolean success) {
		Object context = fsm.getContext();
		if (context instanceof TerminateNotifier) {
			TargetedEvent event = ((TerminateNotifier) context).sendOnTerminate();
			relay.sendEvent(event, success);
		}
	}

	@Override
	public void exceptionThrowed(FSMState<Object, Object> fsm, FSMEvent<Object> event, FSMException exception) {
		onTerminate(fsm, false);
	}
}
