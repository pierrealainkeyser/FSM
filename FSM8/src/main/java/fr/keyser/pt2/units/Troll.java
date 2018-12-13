package fr.keyser.pt2.units;

import fr.keyser.pt2.BoardAccessor;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.ConstInt;
import fr.keyser.pt2.prop.PlugableInt;

public class Troll extends Unit {

    private final PlugableInt payGoldGain = new PlugableInt();

    public Troll() {
	combat = ConstInt.TWO.add(payGoldGain);
    }

    @Override
    public void setBoardAccessor(BoardAccessor accessor) {
	payGoldGain.setSupplier(accessor.board(LocalBoard::getPayGoldGain));
    }
}
