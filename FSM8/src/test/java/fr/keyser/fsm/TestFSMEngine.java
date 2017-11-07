package fr.keyser.fsm;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class TestFSMEngine {

    /**
     * Création d'une FSM à 2 états, qui navigue automatiquement de l'état initial à
     * l'état final en appelant les transitions
     */
    @Test
    public void simple() {

	String[] leaving = new String[1];
	@SuppressWarnings("unchecked")
	FSMEvent<String>[] event = new FSMEvent[1];

	FSMInstanceKey key = new FSMInstanceKey(new FSMKey("simple"), "0");

	FSMBuilder<String, String, Object> builder = new FSMBuilder<>();
	builder.instanceKey(() -> key)
		.state("initial").onEnter((i, e) -> i.sendEvent("next", 1))
		.transition("next", "terminal", (i, e) -> event[0] = e)
		.and()
		.state("terminal").terminal();

	// on peut modifier l'état
	builder.state("initial").onExit((i, e) -> leaving[0] = i.getCurrentState());

	FSM<String, String, Object> fsm = builder.build();
	FSMInstance<String, String, Object> start = fsm.start("myself");
	Assert.assertTrue("L'instance est terminée", start.isDone());
	Assert.assertEquals("L'état à changer", "terminal", start.getCurrentState());
	Assert.assertEquals("L'événement onExit est appelé", "initial", leaving[0]);
	Assert.assertEquals("L'événement onTransition est appelé", new FSMEvent<>("next", 1), event[0]);
	Assert.assertEquals(1l, start.getTransitionCount());
	Assert.assertEquals(key, start.getKey());
    }
}
