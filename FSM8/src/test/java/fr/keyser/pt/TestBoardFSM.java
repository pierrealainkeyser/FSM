package fr.keyser.pt;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import fr.keyser.pt.fsm.BoardFSM;
import fr.keyser.pt.fsm.BuildCommand;
import fr.keyser.pt.fsm.DoDeployCardCommand;
import fr.keyser.pt.fsm.DraftCommand;
import fr.keyser.pt.fsm.NoopCommand;
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

    @Test
    public void testFullTurn() {
	BoardContract board = Mockito.mock(BoardContract.class);

	List<PlayerBoardContract> ps = new ArrayList<>();
	for (int i = 0; i < 3; ++i)
	    ps.add(Mockito.mock(PlayerBoardContract.class));

	for (PlayerBoardContract p : ps)
	    Mockito.when(p.hasInputActions()).thenReturn(false);

	Mockito.when(board.getPlayers()).thenReturn(ps.stream());

	BoardFSM fsm = new BoardFSM(board);
	Assertions.assertEquals(3, fsm.getPlayers().size());
	fsm.start();

	Mockito.verify(board).resetCounters();
	Mockito.verify(board).distributeCards();

	Assertions.assertEquals(BoardFSM.DRAFT, fsm.getPhase());

	for (int i = 0; i < 5; ++i) {
	    for (PlayerBoardFSM p : fsm.getPlayers())
		p.receiveInput(new DraftCommand(0));
	}

	Mockito.verify(board, Mockito.times(4)).passCardsToNext();

	for (PlayerBoardContract p : ps)
	    Mockito.verify(p, Mockito.times(4)).processDraft(Mockito.anyInt());

	Assertions.assertEquals(BoardFSM.DEPLOY, fsm.getPhase());

	receiveAll(fsm, new DoDeployCardCommand(new ArrayList<>(), -1));

	Mockito.verify(board).warPhase();
	Assertions.assertEquals(BoardFSM.WAR, fsm.getPhase());

	receiveAll(fsm, new NoopCommand());

	Assertions.assertEquals(BoardFSM.GOLD, fsm.getPhase());

	receiveAll(fsm, new NoopCommand());

	Assertions.assertEquals(BoardFSM.BUILDING, fsm.getPhase());

	receiveAll(fsm, new BuildCommand());

	Assertions.assertEquals(BoardFSM.DRAFT, fsm.getPhase());

    }

    private void receiveAll(BoardFSM fsm, Object cmd) {
	for (PlayerBoardFSM p : fsm.getPlayers()) {
	    Assertions.assertEquals(cmd.getClass(), p.getExpectedInput());
	    p.receiveInput(cmd);
	}
    }

}
