package fr.keyser.pt2.buildings;

import fr.keyser.pt2.BoardAccessor;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.PlugableInt;

public class Tavern extends Building {

    private final PlugableInt totalFood = new PlugableInt();

    public Tavern() {
	warGoldGain = totalFood;
	warLegend = totalFood.when(isLevel2());
    }

    @Override
    public void setBoardAccessor(BoardAccessor accessor) {
	totalFood.setSupplier(accessor.board(LocalBoard::getFood));
    }

}
