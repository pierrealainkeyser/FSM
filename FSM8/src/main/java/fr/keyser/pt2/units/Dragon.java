package fr.keyser.pt2.units;

import fr.keyser.pt2.BoardAccessor;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.PlugableBool;

public class Dragon extends Unit {

    private final PlugableBool hasCrystal = new PlugableBool();

    public Dragon() {
	combat = ConstInt.SEVEN.when(hasCrystal);
    }

    @Override
    public void setBoardAccessor(BoardAccessor accessor) {
	hasCrystal.setSupplier(accessor.board(LocalBoard::getCrystal).gte(ConstInt.ONE));
    }
}
