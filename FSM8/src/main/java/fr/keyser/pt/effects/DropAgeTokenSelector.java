package fr.keyser.pt.effects;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.CardPositionSelector;
import fr.keyser.pt.PlayerBoard;

public class DropAgeTokenSelector implements CardPositionSelector {

    private final int count;

    public DropAgeTokenSelector(int count) {
	this.count = count;
    }

    public int getCount() {
	return count;
    }

    @Override
    public void process(PlayerBoard board, CardPosition position) {
	board.find(position).getCard().get().doAge(count);
    }
}
