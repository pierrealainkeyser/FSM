package fr.keyser.pt;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.keyser.pt.fsm.BoardFSM;
import fr.keyser.pt.fsm.DoDeployCardCommand;
import fr.keyser.pt.fsm.DraftCommand;
import fr.keyser.pt.fsm.PlayerBoardFSM;

public class TestBoardFSM {

    @Test
    public void testCompleteDraftFor2() {
	Board board = new Board(MetaDeck.createDefault());
	PlayerBoard p0 = board.addNewPlayer();
	PlayerBoard p1 = board.addNewPlayer();

	BoardFSM fsm = new BoardFSM(board);
	fsm.start();
	PlayerBoardFSM fsm0 = fsm.getPlayers().get(0);
	PlayerBoardFSM fsm1 = fsm.getPlayers().get(1);

	for (int i = 0; i < 4; ++i) {

	    Assertions.assertEquals(BoardFSM.DRAFT, fsm.getPhase());
	    Assertions.assertEquals(DraftCommand.class, fsm0.getExpectedInput());
	    Assertions.assertEquals(DraftCommand.class, fsm1.getExpectedInput());

	    List<MetaCard> d0 = p0.getToDraft();
	    fsm0.receiveInput(new DraftCommand(d0.get(0), d0.get(1)));
	    Assertions.assertEquals(BoardFSM.DRAFT, fsm.getPhase());
	    Assertions.assertNull(fsm0.getExpectedInput());
	    Assertions.assertEquals(DraftCommand.class, fsm1.getExpectedInput());

	    List<MetaCard> d1 = p1.getToDraft();
	    fsm1.receiveInput(new DraftCommand(d1.get(0), d1.get(1)));
	}

	Assertions.assertEquals(BoardFSM.DEPLOY, fsm.getPhase());
	Assertions.assertEquals(DoDeployCardCommand.class, fsm0.getExpectedInput());
	Assertions.assertEquals(DoDeployCardCommand.class, fsm1.getExpectedInput());

    }

}
