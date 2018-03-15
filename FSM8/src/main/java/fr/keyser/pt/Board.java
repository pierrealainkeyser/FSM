package fr.keyser.pt;

import java.util.List;
import java.util.stream.Stream;

import fr.keyser.pt.SpecialEffectScope.When;

public class Board implements BoardContract {

    private Turn turn;

    private List<PlayerBoard> players;

    private MetaDeck deck;

    /* (non-Javadoc)
     * @see fr.keyser.pt.BoardContract#distributeCards()
     */
    @Override
    public void distributeCards() {

    }

    /* (non-Javadoc)
     * @see fr.keyser.pt.BoardContract#passCardsToNext()
     */
    @Override
    public void passCardsToNext() {

    }

    boolean sameTurn(CardModel model) {
	return turn.getTurn() == model.getPlayedTurn();
    }

    /* (non-Javadoc)
     * @see fr.keyser.pt.BoardContract#resetCounters()
     */
    @Override
    public void resetCounters() {
	players.forEach(PlayerBoard::resetCounters);
    }

    /* (non-Javadoc)
     * @see fr.keyser.pt.BoardContract#newTurn()
     */
    @Override
    public void newTurn() {
	turn.setTurn(turn.getTurn() + 1);
    }

    /* (non-Javadoc)
     * @see fr.keyser.pt.BoardContract#isLastTurn()
     */
    @Override
    public boolean isLastTurn() {
	return turn.getTurn() == 4;
    }

    /* (non-Javadoc)
     * @see fr.keyser.pt.BoardContract#deployPhaseEffect()
     */
    @Override
    public void deployPhaseEffect() {

	for (PlayerBoard player : players) {
	    player.clearInputActions();
	    player.computeValues();
	    player.fireEffect(When.DEPLOYEMENT);
	}
    }

    /* (non-Javadoc)
     * @see fr.keyser.pt.BoardContract#endOfDeployPhase()
     */
    @Override
    public void endOfDeployPhase() {
	for (PlayerBoard player : players)
	    player.computeDeployGain();
    }

    void moveToDiscard(MetaCard meta) {
	deck.getDiscarded().add(meta);
    }

    /* (non-Javadoc)
     * @see fr.keyser.pt.BoardContract#warPhase()
     */
    @Override
    public void warPhase() {
	for (PlayerBoard player : players)
	    player.computeValues();

	for (int i = 0, size = players.size(); i < size; ++i) {
	    PlayerBoard p = players.get(i);
	    p.setVictoriousWar(warWinned(i));
	    p.computeWarGain();
	}
    }

    /* (non-Javadoc)
     * @see fr.keyser.pt.BoardContract#goldPhase()
     */
    @Override
    public void goldPhase() {
	for (PlayerBoard player : players)
	    player.gainGold();

    }

    /* (non-Javadoc)
     * @see fr.keyser.pt.BoardContract#buildPhase()
     */
    @Override
    public void buildPhase() {
    }

    /* (non-Javadoc)
     * @see fr.keyser.pt.BoardContract#agePhase()
     */
    @Override
    public void agePhase() {
	for (PlayerBoard player : players) {
	    player.clearInputActions();
	    player.collectDying();
	    player.fireEffect(When.AGING);
	}
    }

    /* (non-Javadoc)
     * @see fr.keyser.pt.BoardContract#endAgePhase()
     */
    @Override
    public void endAgePhase() {
	for (PlayerBoard player : players) {
	    player.computeDyingGain();
	    player.removeDead();
	}
    }

    private int warWinned(int playerIndex) {

	int size = players.size();

	int current = players.get(playerIndex).getCombat();
	int right = players.get((playerIndex + 1) % size).getCombat();
	int wins = 0;
	if (size > 2) {
	    int left = players.get((size + (playerIndex - 1)) % size).getCombat();

	    if (current >= left)
		++wins;
	    if (current >= right)
		++wins;

	} else {
	    if (current >= 2 * right)
		wins = 2;
	    else if (current >= right)
		wins = 1;
	}
	return wins;
    }

    /* (non-Javadoc)
     * @see fr.keyser.pt.BoardContract#getPlayers()
     */
    @Override
    public Stream<? extends PlayerBoardContract> getPlayers() {
	return players.stream();
    }

    public int getTurnValue() {
	return turn.getTurn();
    }

    public List<Building> getAvailableBuildings() {
	return deck.getBuildings();
    }

}
