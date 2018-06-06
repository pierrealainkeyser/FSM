package fr.keyser.pt.view;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.keyser.bus.PluggableBus;
import fr.keyser.pt.Board;
import fr.keyser.pt.CardPosition.Position;
import fr.keyser.pt.DoDeployCard;
import fr.keyser.pt.MetaCard;
import fr.keyser.pt.MetaDeck;
import fr.keyser.pt.fsm.BoardFSM;
import fr.keyser.pt.fsm.DoDeployCardCommand;
import fr.keyser.pt.fsm.DraftCommand;
import fr.keyser.pt.fsm.PlayerBoardFSM;

public class TestGameHub {

    private static final Logger logger = LoggerFactory.getLogger(TestGameHub.class);

    private static class TracerEndpoint implements GameEndpoint {

	public TracerEndpoint(GameHub hub, UUID uuid) {
	    this.hub = hub;
	    this.uuid = uuid;
	    hub.register(uuid, this);
	}

	public void receive(Object input) {
	    hub.receive(uuid, input);
	}

	private BoardView view;

	private final GameHub hub;

	private final UUID uuid;

	@Override
	public void send(BoardView view) throws Exception {
	    ObjectMapper om = new ObjectMapper();
	    om.setDefaultPropertyInclusion(Include.NON_EMPTY);

	    this.view = view;
	    try {
		logger.info("{} writeValueAsString : {}", uuid, om.writeValueAsString(view));
	    } catch (Exception e) {
		logger.error("writeValueAsString", e);
	    }

	}

	public BoardView getView() {
	    return view;
	}
    }

    @Test
    public void testFSMIntegration() {
	PluggableBus bus = new PluggableBus();

	Board board = new Board(bus, MetaDeck.createDefault());
	board.addNewPlayer();
	board.addNewPlayer();

	BoardFSM fsm = new BoardFSM(board);

	List<PlayerBoardFSM> players = fsm.getPlayers();
	GameHub hub = new GameHub(bus, players);
	fsm.start();

	ObjectMapper om = new ObjectMapper();
	om.setDefaultPropertyInclusion(Include.NON_EMPTY);

	TracerEndpoint e0 = new TracerEndpoint(hub, players.get(0).getUUID());
	TracerEndpoint e1 = new TracerEndpoint(hub, players.get(1).getUUID());

	for (int i = 0; i < 4; ++i) {
	    List<MetaCard> toDraft0 = e0.getView().getToDraft();
	    List<MetaCard> toDraft1 = e1.getView().getToDraft();

	    e0.receive(new DraftCommand(toDraft0.get(0), toDraft0.get(1)));
	    e1.receive(new DraftCommand(toDraft1.get(0), toDraft1.get(1)));
	}

	DoDeployCardCommand deploy0 = deployAll(e0);
	DoDeployCardCommand deploy1 = deployAll(e1);

	e0.receive(deploy0);
	e1.receive(deploy1);

    }

    private DoDeployCardCommand deployAll(TracerEndpoint ep) {
	List<MetaCard> deploy = ep.getView().getToDeploy();
	return new DoDeployCardCommand(Arrays.asList(
	        new DoDeployCard(deploy.get(0), Position.FRONT.index(0)),
	        new DoDeployCard(deploy.get(1), Position.FRONT.index(1)),
	        new DoDeployCard(deploy.get(2), Position.BACK.index(0)),
	        new DoDeployCard(deploy.get(3), Position.BACK.index(1))), deploy.get(4).getId());
    }

}
