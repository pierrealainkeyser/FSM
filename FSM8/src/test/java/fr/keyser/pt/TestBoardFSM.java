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

	PlayerBoardFSM fsm0 = fsm.getPlayers().get(0);
	fsm.start();

	Assert.assertEquals(DraftCommand.class, fsm0.getExpectedInput());
    }

}
