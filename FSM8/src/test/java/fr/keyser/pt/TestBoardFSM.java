package fr.keyser.pt;

import org.junit.Assert;
import org.junit.Test;

import fr.keyser.pt.fsm.BoardFSM;
import fr.keyser.pt.fsm.DraftCommand;
import fr.keyser.pt.fsm.PlayerBoardFSM;

public class TestBoardFSM {

    @Test
    public void testBasic() {
	Board board = new Board(MetaDeck.createDefault());
	board.addNewPlayer();
	board.addNewPlayer();

	BoardFSM fsm = new BoardFSM(board);
	fsm.start();
	PlayerBoardFSM fsm0 = fsm.getPlayers().get(0);
	PlayerBoardFSM fsm1 = fsm.getPlayers().get(1);

	Assert.assertEquals(BoardFSM.DRAFT, fsm.getPhase());
	Assert.assertEquals(DraftCommand.class, fsm0.getExpectedInput());
	Assert.assertEquals(DraftCommand.class, fsm1.getExpectedInput());
    }

}
