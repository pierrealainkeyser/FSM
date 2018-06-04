package fr.keyser.pt.fsm;

import java.util.UUID;
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
import fr.keyser.pt.event.PlayerAppearanceEvent;
import fr.keyser.pt.event.PlayerBuildPlanEvent;
import fr.keyser.pt.event.PlayerDoDeployEvent;
import fr.keyser.pt.event.PlayerDoDraftEvent;
import fr.keyser.pt.event.PlayerIdleEvent;
import fr.keyser.pt.event.PlayerInputActionEvent;

public class PlayerBoardFSM implements PlayerBoardAcces {

    private static final String EVENT_ARGS = "args";
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

    private final static String END_OF_GAME = "EOG";
    private final static String END_OF_TURN = "EOT";

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
	StateBuilder<String, PlayerEvent> eog = builder.state(END_OF_GAME);
	StateBuilder<String, PlayerEvent> eot = builder.state(END_OF_TURN);

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
	waitAge(ec, age, eot);

	init.transition(PlayerEvent.NEXT_PHASE, draft);

	eot.transition(PlayerEvent.NEXT_PHASE, draft);
	eot.transition(PlayerEvent.SKIP, eog);

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

    private void waitAge(DelayedEventConsumer<String, PlayerEvent> ec, StateBuilder<String, PlayerEvent> age,
            StateBuilder<String, PlayerEvent> eot) {

	StateBuilder<String, PlayerEvent> done = age.sub(DONE_USER);

	conditionalInputProcessing(ec, age.sub(WAITING_USER), done);

	signalBoardAndTransitionNextPhaseTo(eot, done);
    }

    public void conditionalInputProcessing(DelayedEventConsumer<String, PlayerEvent> ec, StateBuilder<String, PlayerEvent> waitingEffect,
            StateBuilder<String, PlayerEvent> done) {
	waitingEffect.onEntry(() -> {
	    if (!contract.hasInputActions())
		ec.push(PlayerEvent.SKIP);
	    else {
		fireInputActions();
	    }
	}).transition(PlayerEvent.SKIP, done);

	waitingEffect.onEntry(expect(CardActionCommand.class, "input"))
	        .onExit(expect(null, null))
	        .transition(PlayerEvent.RECEIVE_INPUT, done)
	        .guard(this::validateArgs)
	        .onTransition(withArgs(this::processInput));
    }

    private void fireInputActions() {
	boardFSM.forward(new PlayerInputActionEvent(contract.getUUID(), contract.getInputActions()));
    }

    private void waitDeploy(DelayedEventConsumer<String, PlayerEvent> ec, StateBuilder<String, PlayerEvent> deploy,
            StateBuilder<String, PlayerEvent> war) {

	StateBuilder<String, PlayerEvent> play = deploy.sub(PLAY);
	StateBuilder<String, PlayerEvent> played = deploy.sub(PLAYED);
	StateBuilder<String, PlayerEvent> waitingEffect = played.sub(WAITING_USER);
	StateBuilder<String, PlayerEvent> done = played.sub(DONE_USER);

	play.onEntry(expect(DoDeployCardCommand.class, "deploy"))
	        .transition(PlayerEvent.RECEIVE_INPUT, waitingEffect)
	        .guard(this::validateArgs)
	        .onTransition(withArgs(this::processDeploy));

	played.onEntry(contract::deployPhase)
	        .onExit(contract::endOfDeployPhase);

	conditionalInputProcessing(ec, waitingEffect, done);

	signalBoardAndTransitionNextPhaseTo(war, done);
    }

    private SimpleAction expect(Class<?> expectedInput, String appearance) {
	return () -> {
	    this.expectedInput = expectedInput;
	    this.appearance = appearance;

	    fireStatusEvent();
	};
    }

    public void fireEvents() {
	fireStatusEvent();
	fireInputActions();
    }

    private void fireStatusEvent() {
	UUID uuid = getUUID();
	boardFSM.forward(new PlayerIdleEvent(uuid, expectedInput == null));
	if (DoDeployCardCommand.class.equals(expectedInput)) {
	    boardFSM.forward(new PlayerDoDeployEvent(uuid, contract.getToDeploy()));
	} else if (BuildCommand.class.equals(expectedInput)) {
	    boardFSM.forward(new PlayerBuildPlanEvent(uuid, contract.getBuildPlan()));
	} else if (DraftCommand.class.equals(expectedInput)) {
	    boardFSM.forward(new PlayerDoDraftEvent(uuid, contract.getToDraft()));
	}

	boardFSM.forward(new PlayerAppearanceEvent(uuid, appearance));
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
	        .onTransition(withArgs(consumer));

	signalBoardAndTransitionNextPhaseTo(to, done);
	done.transition(PlayerEvent.LOOP, waiting);

    }

    public void signalBoardAndTransitionNextPhaseTo(StateBuilder<String, PlayerEvent> to, StateBuilder<String, PlayerEvent> done) {
	done.onEntry(boardFSM::next)
	        .transition(PlayerEvent.NEXT_PHASE, to);
    }

    private void processInput(CardActionCommand command) {
	for (CardAction action : command.getActions())
	    contract.processCardAction(action);
    }

    private void processBuilding(BuildCommand command) {
	Integer index = command.getIndex();
	if (index != null)
	    contract.processBuild(index);
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

    @Override
    public void receiveInput(Object input) {
	stateMachine.push(Event.create(PlayerEvent.RECEIVE_INPUT).put(EVENT_ARGS, input));
    }

    private boolean validateArgs(Event<PlayerEvent> event) {
	Object args = event.get(EVENT_ARGS);
	if (args == null)
	    return false;
	else
	    return expectedInput.isAssignableFrom(args.getClass());
    }

    private <T> OnSimpleTransitionAction<PlayerEvent> withArgs(Consumer<T> consumer) {

	return event -> {
	    @SuppressWarnings("unchecked")
	    T command = (T) event.get(EVENT_ARGS);
	    consumer.accept(command);
	};
    }

    @Override
    public void refresh() {
	contract.refresh();

	// gestion de l'état d'activité
	boardFSM.refresh();
    }

    @Override
    public UUID getUUID() {
	return contract.getUUID();
    }
}
