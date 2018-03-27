package fr.keyser.pt;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

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

	    Assert.assertEquals(BoardFSM.DRAFT, fsm.getPhase());
	    Assert.assertEquals(DraftCommand.class, fsm0.getExpectedInput());
	    Assert.assertEquals(DraftCommand.class, fsm1.getExpectedInput());

	    List<MetaCard> d0 = p0.getToDraft();
	    fsm0.receiveInput(new DraftCommand(d0.get(0).getId(), d0.get(1).getId()));
	    Assert.assertEquals(BoardFSM.DRAFT, fsm.getPhase());
	    Assert.assertNull(fsm0.getExpectedInput());
	    Assert.assertEquals(DraftCommand.class, fsm1.getExpectedInput());

	    List<MetaCard> d1 = p1.getToDraft();
	    fsm1.receiveInput(new DraftCommand(d1.get(0).getId(), d1.get(1).getId()));
	}

	Assert.assertEquals(BoardFSM.DEPLOY, fsm.getPhase());
	Assert.assertEquals(DoDeployCardCommand.class, fsm0.getExpectedInput());
	Assert.assertEquals(DoDeployCardCommand.class, fsm1.getExpectedInput());

    }

}
