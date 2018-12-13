package fr.keyser.pt2.buildings;

import fr.keyser.pt2.BoardAccessor;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.PlugableInt;

public class Tavern extends Building {

    private final PlugableInt totalFood = new PlugableInt();

    public Tavern() {
	super(cost().food(1), cost().wood(1).food(1));
	warGoldGain = totalFood;
	warLegend = totalFood.when(getLevel2());
    }

    @Override
    public void setBoardAccessor(BoardAccessor accessor) {
	totalFood.setSupplier(accessor.board(LocalBoard::getFood));
    }

}
