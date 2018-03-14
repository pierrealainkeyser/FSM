package fr.keyser.pt.effects;

import java.util.List;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.CardPositionSelector;
import fr.keyser.pt.PlayerBoard;

public class PreserveFromDeathSelector implements CardPositionSelector {

    private final List<CardPosition> dyings;

    public PreserveFromDeathSelector(List<CardPosition> dyings) {
	this.dyings = dyings;
    }

    @Override
    public void process(PlayerBoard board, CardPosition position) {
	board.preserveFromDeath(position);
    }

    public  List<CardPosition> getDyings() {
        return dyings;
    }
}
