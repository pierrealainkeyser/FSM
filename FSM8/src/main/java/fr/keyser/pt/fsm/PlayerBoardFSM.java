package fr.keyser.pt.fsm;

import java.util.function.Consumer;

import fr.keyser.fsm.Event;
import fr.keyser.fsm.OnSimpleTransitionAction;
import fr.keyser.fsm.SimpleAction;
import fr.keyser.fsm.StateMachine;
import fr.keyser.fsm.StateMachineBuilder;
import fr.keyser.fsm.StateMachineBuilder.StateBuilder;
import fr.keyser.pt.CardAction;
import fr.keyser.pt.DoDeployCard;
import fr.keyser.pt.PlayerBoard;

public class PlayerBoardFSM {

    private enum PlayerEvent {
	USER_INPUT, WAIT_FOR_BUILDING, WAIT_FOR_DEPLOY, WAIT_FOR_CARD, WAIT_FOR_INPUT, WAIT_NOOP
    }

    private enum PlayerState {
	BUILDING, CARD, IDLE, DEPLOY, INPUT, NOOP, WAITING_USER
    }

    private Class<?> expectedInput;

    private final PlayerBoard player;

    private final StateMachine<PlayerState, PlayerEvent> stateMachine;

    private final BoardFSM boardFSM;

    public PlayerBoardFSM(PlayerBoard player, BoardFSM boardFSM) {
	this.player = player;
	this.boardFSM = boardFSM;

	StateMachineBuilder<PlayerState, PlayerEvent> builder = new StateMachineBuilder<>();
	StateBuilder<PlayerState, PlayerEvent> idle = builder.state(PlayerState.IDLE);

	StateBuilder<PlayerState, PlayerEvent> waitingUser = builder.state(PlayerState.WAITING_USER).onExit(expectedInput(null));

	// TODO
	StateBuilder<PlayerState, PlayerEvent> deploy = waitingUser.sub(PlayerState.DEPLOY);

	StateBuilder<PlayerState, PlayerEvent> input = waitingUser.sub(PlayerState.INPUT);

	// TODO
	StateBuilder<PlayerState, PlayerEvent> building = waitingUser.sub(PlayerState.BUILDING);
	StateBuilder<PlayerState, PlayerEvent> card = waitingUser.sub(PlayerState.CARD);
	StateBuilder<PlayerState, PlayerEvent> noop = waitingUser.sub(PlayerState.NOOP);

	idle.transition(PlayerEvent.WAIT_FOR_DEPLOY, deploy);
	idle.transition(PlayerEvent.WAIT_FOR_INPUT, input);
	idle.transition(PlayerEvent.WAIT_FOR_BUILDING, building);
	idle.transition(PlayerEvent.WAIT_FOR_CARD, card);
	idle.transition(PlayerEvent.WAIT_NOOP, noop);

	onInput(input, idle, CardActionCommand.class, this::processInput);
	onInput(deploy, idle, DoDeployCardCommand.class, this::processDeploy);
	onInput(card, idle, DraftCommand.class, this::processDraft);
	onInput(noop, idle, Object.class, e -> {
	});
	stateMachine = builder.build();
	stateMachine.enterInitialState();
    }

    private SimpleAction expectedInput(Class<?> expectedInput) {
	return () -> this.expectedInput = expectedInput;
    }

    public Class<?> getExpectedInput() {
	return expectedInput;
    }

    private <T> void onInput(StateBuilder<PlayerState, PlayerEvent> from, StateBuilder<PlayerState, PlayerEvent> to, Class<T> type,
            Consumer<T> consumer) {
	from.onEntry(expectedInput(type));
	from.transition(PlayerEvent.USER_INPUT, to).guard(this::validateArgs).onTransition(wrapConsumer(consumer));
    }

    private void processInput(CardActionCommand command) {
	for (CardAction action : command.getActions())
	    player.processCardAction(action);
    }

    private void processDraft(DraftCommand command) {
	player.processDraft(command.getDraft());

	if (command.getDiscard() != null)
	    player.processDiscard(command.getDiscard());
    }

    private void processDeploy(DoDeployCardCommand command) {
	for (DoDeployCard action : command.getActions())
	    player.processDeployCardAction(action);

	player.keepToDeploy(command.getKeep());
    }

    public void receiveInput(Object input) {
	stateMachine.push(Event.create(PlayerEvent.USER_INPUT).put("args", input));
    }

    private boolean validateArgs(Event<PlayerEvent> event) {
	Object args = event.get("args");
	if (args == null)
	    return false;
	else
	    return expectedInput.isAssignableFrom(args.getClass());
    }

    public void waitForBuilding() {
	stateMachine.push(PlayerEvent.WAIT_FOR_BUILDING);
    }

    public void waitForDeploy() {
	stateMachine.push(PlayerEvent.WAIT_FOR_DEPLOY);
    }

    public void waitForCard() {
	stateMachine.push(PlayerEvent.WAIT_FOR_CARD);
    }

    public void waitForInput() {
	stateMachine.push(PlayerEvent.WAIT_FOR_INPUT);
    }

    public void waitFor() {
	stateMachine.push(PlayerEvent.WAIT_NOOP);
    }

    private <T> OnSimpleTransitionAction<PlayerEvent> wrapConsumer(Consumer<T> consumer) {

	return event -> {
	    @SuppressWarnings("unchecked")
	    T command = (T) event.get("args");
	    consumer.accept(command);

	    boardFSM.next();
	};
    }

    public PlayerBoard getPlayer() {
	return player;
    }
}
