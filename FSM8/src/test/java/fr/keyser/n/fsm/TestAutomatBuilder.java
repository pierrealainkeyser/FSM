package fr.keyser.n.fsm;

import static fr.keyser.pt2.automat.PTMultiPlayersAutomatBuilder.draft;
import static fr.keyser.pt2.automat.PTMultiPlayersAutomatBuilder.forPlayer;

import org.junit.jupiter.api.Test;

import fr.keyser.n.fsm.automat.Automat;
import fr.keyser.n.fsm.automat.AutomatContainer;
import fr.keyser.n.fsm.container.AutomatContainerBuilder;
import fr.keyser.n.fsm.container.AutomatContainerBuilder.StateConfigurer;
import fr.keyser.pt2.automat.PTMultiPlayersAutomatBuilder;

public class TestAutomatBuilder {
    @Test
    public void testPT() {
	int nbPlayers = 3;
	PTMultiPlayersAutomatBuilder pt = new PTMultiPlayersAutomatBuilder(nbPlayers);

	Automat automat = pt.build();
	System.out.println(automat);

	AutomatContainerBuilder acb = new AutomatContainerBuilder(automat);
	acb.state(draft(0)).entry(() -> System.out.println("distribute"));

	for (int step = 0; step < 4; ++step) {
	    State draftStep = draft(step);
	    acb.state(draftStep).exit(() -> System.out.println("passToNext"));

	    for (int player = 0; player < nbPlayers; ++player) {
		int play = player;
		StateConfigurer playerDraftStepConfigurer = acb.state(forPlayer(draftStep, player));
		playerDraftStepConfigurer.entry(is -> System.out.println("expecting selection " + play + " -> " + is.getId()));
		playerDraftStepConfigurer.exit(() -> System.out.println("Selection done " + play));
	    }
	}
	AutomatContainer ac = acb.build();
	ac.start();

	ac.receive(Event.event("done").id(new InstanceId("1")));
	ac.receive(Event.event("done").id(new InstanceId("2")));
	ac.receive(Event.event("done").id(new InstanceId("3")));
    }
}
