package fr.keyser.n.fsm;

import org.junit.jupiter.api.Test;

import fr.keyser.n.fsm.automat.Automat;
import fr.keyser.pt2.automat.PTMultiPlayersAutomatBuilder;

public class TestAutomatBuilder {
    @Test
    public void testPT() {
	int nbPlayers = 3;
	PTMultiPlayersAutomatBuilder pt = new PTMultiPlayersAutomatBuilder(nbPlayers);

	Automat automat = pt.build();
	System.out.println(automat);
    }
}
