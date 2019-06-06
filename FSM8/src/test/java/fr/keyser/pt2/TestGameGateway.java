package fr.keyser.pt2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.keyser.n.fsm.automat.Automat;
import fr.keyser.pt2.automat.GameGateway;
import fr.keyser.pt2.automat.GameGateway.PlayerGateway;
import fr.keyser.pt2.automat.PTMultiPlayersAutomatBuilder;
import fr.keyser.pt2.units.Alchimist;
import fr.keyser.pt2.units.CaveSpirit;
import fr.keyser.pt2.units.ForestChildren;
import fr.keyser.pt2.view.BuildInstructionDTO;
import fr.keyser.pt2.view.PickInstructionDTO;
import fr.keyser.pt2.view.PlayInstructionDTO;

public class TestGameGateway {

    private final static Logger logger = LoggerFactory.getLogger(TestGameGateway.class);

    private static List<String> createUnits() {
	List<String> str = new ArrayList<>();
	for (int x = 0; x < 4; ++x) {
	    IntStream.range(0, 5).forEach(i -> str.add("CaveSpirit"));
	    IntStream.range(0, 5).forEach(i -> str.add("Alchimist"));
	    IntStream.range(0, 5).forEach(i -> str.add("ForestChildren"));
	}
	return str;
    }

    @Test
    public void test() {

	CardProvider cp = new CardProvider();
	cp.addUnit(CaveSpirit::new);
	cp.addUnit(Alchimist::new);
	cp.addUnit(ForestChildren::new);

	DeckMemento dm = new DeckMemento();
	dm.setDiscardeds(new ArrayList<>());
	dm.setBuildings(Arrays.asList());
	dm.setUnits(createUnits());

	LocalGameSettings settings = new LocalGameSettings(dm, 3);
	LocalGame localGame = new LocalGame(cp, settings);

	Automat automat = new PTMultiPlayersAutomatBuilder(settings.getNbPlayers()).build();
	logger.debug("Automat :\n{}", automat);

	GameGateway gateway = new GameGateway(automat, localGame);
	gateway.start();

	for (int t = 0; t < 4; ++t) {
	    processOnTurn(gateway);
	}
    }

    private void processOnTurn(GameGateway gateway) {
	// draft
	List<PlayerGateway> players = gateway.getPlayers();
	for (int i = 0; i < 4; ++i) {
	    int count = 0;
	    for (PlayerGateway pg : players) {
		Assertions.assertTrue(pg.isWaiting());
		Assertions.assertEquals(PickInstructionDTO.class, pg.getExpectedInput());

		PickInstructionDTO pick = new PickInstructionDTO();
		pick.setPick(0);
		Assertions.assertTrue(pg.handleInput(pick));

		// Les 2 premiers attendent
		if (count < 2)
		    Assertions.assertFalse(pg.isWaiting());
		else
		    Assertions.assertTrue(pg.isWaiting());
		++count;
	    }
	}

	// deploy
	for (PlayerGateway pg : players) {
	    Assertions.assertTrue(pg.isWaiting());
	    Assertions.assertEquals(PlayInstructionDTO.class, pg.getExpectedInput());
	    PlayInstructionDTO play = new PlayInstructionDTO();
	    pg.handleInput(play);
	}

	// war
	for (PlayerGateway pg : players) {
	    Assertions.assertTrue(pg.isWaiting());
	    Assertions.assertNull(pg.getExpectedInput());
	    pg.handleInput(null);
	}

	// gold
	for (PlayerGateway pg : players) {
	    Assertions.assertTrue(pg.isWaiting());
	    Assertions.assertNull(pg.getExpectedInput());
	    pg.handleInput(null);
	}

	// build
	for (PlayerGateway pg : players) {
	    Assertions.assertTrue(pg.isWaiting());
	    Assertions.assertEquals(BuildInstructionDTO.class, pg.getExpectedInput());
	    BuildInstructionDTO build = new BuildInstructionDTO();
	    pg.handleInput(build);
	}

	// age
	for (PlayerGateway pg : players) {
	    Assertions.assertTrue(pg.isWaiting());
	    Assertions.assertNull(pg.getExpectedInput());
	    pg.handleInput(null);
	}
    }
}
