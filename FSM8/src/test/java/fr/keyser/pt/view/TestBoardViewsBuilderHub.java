package fr.keyser.pt.view;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.keyser.bus.PluggableBus;
import fr.keyser.pt.Board;
import fr.keyser.pt.BoardBuilder;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.MetaCard;
import fr.keyser.pt.MetaDeck;
import fr.keyser.pt.PlayerBoard;
import fr.keyser.pt.buildings.Mine;
import fr.keyser.pt.event.CardAgeChanged;
import fr.keyser.pt.event.CardRefreshInfo;
import fr.keyser.pt.event.PlayerGoldChanged;
import fr.keyser.pt.event.PlayerLegendChanged;
import fr.keyser.pt.fsm.BoardFSM;
import fr.keyser.pt.fsm.DraftCommand;
import fr.keyser.pt.fsm.PlayerBoardAcces;
import fr.keyser.pt.fsm.PlayerBoardFSM;
import fr.keyser.pt.units.CaveSpirit;

public class TestBoardViewsBuilderHub {

    @Test
    public void testCreation() {

	BoardBuilder builder = new BoardBuilder();

	MetaCard caveSpirit = builder.meta(new CaveSpirit());
	MetaCard mine = builder.meta(new Mine());

	PlayerBoard pb0 = builder.player()
	        .front(caveSpirit)
	        .level2(mine)
	        .build();

	PluggableBus bus = new PluggableBus();
	PlayerBoardAcces p0 = new DefaultPlayerBoardAcces() {
	    @Override
	    public void receiveInput(Object input) {
		DeployedCard first = pb0.units().findFirst().get();
		bus.forward(new CardRefreshInfo(first, pb0));
		bus.forward(new CardAgeChanged(first, pb0, 1));

		bus.forward(new CardRefreshInfo(pb0.buildings().findFirst().get(), pb0));

		bus.forward(new PlayerGoldChanged(pb0, 3));
		bus.forward(new PlayerLegendChanged(pb0, 3));

	    }
	};

	BoardViewsBuilderHub hub = new BoardViewsBuilderHub(bus, Arrays.asList(p0));

	List<BoardView> view = hub.receive(p0.getUUID(), null);
	Assertions.assertEquals(1, view.size());
    }

    @Test
    public void testFSMIntegration() {
	PluggableBus bus = new PluggableBus();

	Board board = new Board(bus, MetaDeck.createDefault());
	board.addNewPlayer();
	board.addNewPlayer();

	BoardFSM fsm = new BoardFSM(board);

	List<PlayerBoardFSM> players = fsm.getPlayers();
	BoardViewsBuilderHub hub = new BoardViewsBuilderHub(bus, players);
	fsm.start();

	PlayerBoardFSM p0 = players.get(0);
	UUID u0 = p0.getUUID();
	BoardView view = hub.refresh(u0);
	Assertions.assertEquals("draftCard", view.getAppeareance());
	List<MetaCard> toDraft = view.getToDraft();
	Assertions.assertEquals(9, toDraft.size());
	Assertions.assertEquals(1, (int) view.getTurn());
	Assertions.assertEquals(BoardFSM.DRAFT, view.getPhase());

	// tout est active
	for (PlayerBoardView pbv : view.getPlayers().values()) {
	    Assertions.assertFalse(pbv.getIdle());
	}

	List<BoardView> views = hub.receive(u0, new DraftCommand(toDraft.get(0), toDraft.get(1)));
	Assertions.assertEquals(2, views.size());
	BoardView newView = views.stream().filter(v -> v.getLocal().equals(u0)).findFirst().get();
	Assertions.assertNull(newView.getAppeareance());
	Assertions.assertNull(newView.getToDraft());
	Assertions.assertEquals(1, (int) newView.getTurn());
	Assertions.assertEquals(BoardFSM.DRAFT, newView.getPhase());

    }

}
