package fr.keyser.pt2.buildings;

import fr.keyser.pt2.BoardAccessor;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.PlugableInt;

public class Cave extends Building {

    private final PlugableInt totalCrystal = new PlugableInt();

    public Cave() {
	crystal = ConstInt.ONE;
	warLegend = totalCrystal.mult(ConstInt.TWO).when(isLevel2());
    }

    @Override
    public void setBoardAccessor(BoardAccessor accessor) {
	totalCrystal.setSupplier(accessor.board(LocalBoard::getCrystal));
    }

}
