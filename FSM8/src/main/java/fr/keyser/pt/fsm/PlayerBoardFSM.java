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
import fr.keyser.pt.PlayerBoardContract;

public class PlayerBoardFSM {

    private enum PlayerEvent {
	USER_INPUT, WAIT_FOR_BUILDING, WAIT_FOR_DEPLOY, WAIT_FOR_CARD, WAIT_FOR_INPUT, WAIT_NOOP
    }

    private final static String BUILDING = "BUILDING";
    private final static String CARD = "CARD";
    private final static String IDLE = "IDLE";
    private final static String DEPLOY = "DEPLOY";
    private final static String INPUT = "INPUT";
    private final static String NOOP = "NOOP";
    private final static String WAITING_USER = "WAITING_USER";

    private Class<?> expectedInput;

    private String appearance;

    private final PlayerBoardContract contract;

    private final StateMachine<String, PlayerEvent> stateMachine;

    private final BoardFSM boardFSM;

    public PlayerBoardFSM(PlayerBoardContract contract, BoardFSM boardFSM) {
	this.contract = contract;
	this.boardFSM = boardFSM;

	StateMachineBuilder<String, PlayerEvent> builder = new StateMachineBuilder<>();
	StateBuilder<String, PlayerEvent> idle = builder.state(IDLE).onEntry(expect(null, "idle"));

	StateBuilder<String, PlayerEvent> waitingUser = builder.state(WAITING_USER);

	// TODO
	StateBuilder<String, PlayerEvent> deploy = waitingUser.sub(DEPLOY);

	StateBuilder<String, PlayerEvent> input = waitingUser.sub(INPUT);

	// TODO
	StateBuilder<String, PlayerEvent> building = waitingUser.sub(BUILDING);
	StateBuilder<String, PlayerEvent> card = waitingUser.sub(CARD);
	StateBuilder<String, PlayerEvent> noop = waitingUser.sub(NOOP);

	idle.transition(PlayerEvent.WAIT_FOR_DEPLOY, deploy);
	idle.transition(PlayerEvent.WAIT_FOR_INPUT, input);
	idle.transition(PlayerEvent.WAIT_FOR_BUILDING, building);
	idle.transition(PlayerEvent.WAIT_FOR_CARD, card);
	idle.transition(PlayerEvent.WAIT_NOOP, noop);

	onInput(input, idle, CardActionCommand.class, "activateCard", this::processInput);
	onInput(deploy, idle, DoDeployCardCommand.class, "deployCard", this::processDeploy);
	onInput(card, idle, DraftCommand.class, "selectCard", this::processDraft);
	onInput(noop, idle, Object.class, "confirmNoop", e -> {
	});
	stateMachine = builder.build();
    }

    private SimpleAction expect(Class<?> expectedInput, String appearance) {
	return () -> {
	    this.expectedInput = expectedInput;
	    this.appearance = appearance;
	};
    }

    public Class<?> getExpectedInput() {
	return expectedInput;
    }

    private <T> void onInput(StateBuilder<String, PlayerEvent> from, StateBuilder<String, PlayerEvent> to, Class<T> type,
            String appeareance, Consumer<T> consumer) {
	from.onEntry(expect(type, appeareance));
	from.transition(PlayerEvent.USER_INPUT, to).guard(this::validateArgs).onTransition(wrapConsumer(consumer));
    }

    private void processInput(CardActionCommand command) {
	for (CardAction action : command.getActions())
	    contract.processCardAction(action);
    }

    private void processDraft(DraftCommand command) {
	contract.processDraft(command.getDraft());

	if (command.getDiscard() != null)
	    contract.processDiscard(command.getDiscard());
    }

    private void processDeploy(DoDeployCardCommand command) {
	for (DoDeployCard action : command.getActions())
	    contract.processDeployCardAction(action);

	contract.keepToDeploy(command.getKeep());
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

    void waitForBuilding() {
	stateMachine.push(PlayerEvent.WAIT_FOR_BUILDING);
    }

    void waitForDeploy() {
	stateMachine.push(PlayerEvent.WAIT_FOR_DEPLOY);
    }

    void waitForCard() {
	stateMachine.push(PlayerEvent.WAIT_FOR_CARD);
    }

    void waitForInput() {
	stateMachine.push(PlayerEvent.WAIT_FOR_INPUT);
    }

    void waitFor() {
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

    public String getAppearance() {
	return appearance;
    }
}
