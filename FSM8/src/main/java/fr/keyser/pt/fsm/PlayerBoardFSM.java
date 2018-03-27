package fr.keyser.pt.fsm;

import java.util.function.Consumer;

import fr.keyser.fsm.DelayedEventConsumer;
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

    private static final String PLAYED = "PLAYED";
    private static final String PLAY = "PLAY";

    private enum PlayerEvent {
	RECEIVE_INPUT, SKIP, LOOP, NEXT_PHASE
    }

    private final static String DRAFT = "DRAFT";
    private final static String WAR = "WAR";
    private final static String GOLD = "GOLD";
    private final static String BUILDING = "BUILDING";
    private final static String DEPLOY = "DEPLOY";
    private final static String AGE = "AGE";

    private final static String WAITING_USER = "WAITING_USER";
    private final static String DONE_USER = "DONE_USER";

    private final static String INIT = "INIT";

    private Class<?> expectedInput;

    private String appearance;

    private final PlayerBoardContract contract;

    private final StateMachine<String, PlayerEvent> stateMachine;

    private final BoardFSM boardFSM;

    public PlayerBoardFSM(PlayerBoardContract contract, BoardFSM boardFSM) {
	this.contract = contract;
	this.boardFSM = boardFSM;

	StateMachineBuilder<String, PlayerEvent> builder = new StateMachineBuilder<>();
	DelayedEventConsumer<String, PlayerEvent> ec = builder.eventConsummer();

	StateBuilder<String, PlayerEvent> init = builder.state(INIT);
	StateBuilder<String, PlayerEvent> draft = builder.state(DRAFT);
	StateBuilder<String, PlayerEvent> deploy = builder.state(DEPLOY);
	StateBuilder<String, PlayerEvent> war = builder.state(WAR);
	StateBuilder<String, PlayerEvent> gold = builder.state(GOLD);
	StateBuilder<String, PlayerEvent> building = builder.state(BUILDING);
	StateBuilder<String, PlayerEvent> age = builder.state(AGE);

	waitInput(draft, deploy, DraftCommand.class, "draftCard", this::processDraft);

	waitDeploy(ec, deploy, war);
	waitInput(war, gold, NoopCommand.class, "confirmWar", this::noOp);
	gold.onEntry(contract::goldPhase);
	waitInput(gold, building, NoopCommand.class, "confirmGold", this::noOp);

	building.onEntry(contract::buildPhase)
	        .onExit(contract::endBuildPhase);
	waitInput(building, age, BuildCommand.class, "build", this::processBuilding);

	age.onEntry(contract::agePhase)
	        .onExit(contract::endAgePhase);

	init.transition(PlayerEvent.NEXT_PHASE, draft);

	stateMachine = builder.build();
    }

    void nextPhase() {
	stateMachine.push(PlayerEvent.NEXT_PHASE);
    }

    void loop() {
	stateMachine.push(PlayerEvent.LOOP);
    }

    private void noOp(NoopCommand noop) {
	// NOOP
    }

    private void waitDeploy(DelayedEventConsumer<String, PlayerEvent> ec, StateBuilder<String, PlayerEvent> deploy,
            StateBuilder<String, PlayerEvent> war) {

	StateBuilder<String, PlayerEvent> play = deploy.sub(PLAY);

	StateBuilder<String, PlayerEvent> played = deploy.sub(PLAYED);
	StateBuilder<String, PlayerEvent> waitingEffect = played.sub(WAITING_USER);
	StateBuilder<String, PlayerEvent> done = played.sub(DONE_USER);

	play.onEntry(expect(DoDeployCard.class, "deploy"))
	        .transition(PlayerEvent.RECEIVE_INPUT, waitingEffect)
	        .guard(this::validateArgs)
	        .onTransition(wrapConsumer(this::processDeploy));

	waitingEffect.onEntry(() -> {
	    if (!contract.hasInputActions()) {
		ec.push(PlayerEvent.SKIP);
	    }
	}).transition(PlayerEvent.SKIP, done);

	waitingEffect.onEntry(expect(CardActionCommand.class, "input"))
	        .onExit(expect(null, null))
	        .transition(PlayerEvent.RECEIVE_INPUT, done)
	        .guard(this::validateArgs)
	        .onTransition(wrapConsumer(this::processInput));

	played.onEntry(contract::deployPhaseEffect)
	        .onExit(contract::endOfDeployPhase);

	done.onEntry(boardFSM::next)
	        .transition(PlayerEvent.NEXT_PHASE, war);
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

    private <T> void waitInput(StateBuilder<String, PlayerEvent> from, StateBuilder<String, PlayerEvent> to, Class<T> type,
            String appeareance, Consumer<T> consumer) {

	StateBuilder<String, PlayerEvent> waiting = from.sub(WAITING_USER);
	StateBuilder<String, PlayerEvent> done = from.sub(DONE_USER);

	waiting.onEntry(expect(type, appeareance))
	        .onExit(expect(null, null))
	        .transition(PlayerEvent.RECEIVE_INPUT, done)
	        .guard(this::validateArgs)
	        .onTransition(wrapConsumer(consumer));

	done.onEntry(boardFSM::next)
	        .transition(PlayerEvent.LOOP, waiting);
	done.transition(PlayerEvent.NEXT_PHASE, to);

    }

    private void processInput(CardActionCommand command) {
	for (CardAction action : command.getActions())
	    contract.processCardAction(action);
    }

    private void processBuilding(BuildCommand command) {
	Integer index = command.getIndex();
	if (index != null)
	    contract.doBuild(index);
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
	stateMachine.push(Event.create(PlayerEvent.RECEIVE_INPUT).put("args", input));
    }

    private boolean validateArgs(Event<PlayerEvent> event) {
	Object args = event.get("args");
	if (args == null)
	    return false;
	else
	    return expectedInput.isAssignableFrom(args.getClass());
    }

    private <T> OnSimpleTransitionAction<PlayerEvent> wrapConsumer(Consumer<T> consumer) {

	return event -> {
	    @SuppressWarnings("unchecked")
	    T command = (T) event.get("args");
	    consumer.accept(command);
	};
    }

    public String getAppearance() {
	return appearance;
    }
}
