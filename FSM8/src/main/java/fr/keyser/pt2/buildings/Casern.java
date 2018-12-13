package fr.keyser.pt2.buildings;

import fr.keyser.pt2.BoardAccessor;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.PlugableInt;

public class Casern extends Building {

    private final PlugableInt totalUnitsAbove3 = new PlugableInt();

    private final PlugableInt totalWood = new PlugableInt();

    public Casern() {
	payGoldGain = totalWood;
	payLegend = totalUnitsAbove3.when(isLevel2());
    }

    @Override
    public void setBoardAccessor(BoardAccessor accessor) {
	totalWood.setSupplier(accessor.board(LocalBoard::getWood));
	totalUnitsAbove3.setSupplier(accessor.board(LocalBoard::getUnitsAbove3));
    }

}
