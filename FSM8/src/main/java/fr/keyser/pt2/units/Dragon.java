package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.prop.BoolSupplier;
import fr.keyser.pt2.prop.ConstInt;

public class Dragon extends Unit {
    public Dragon() {
	super(ConstInt.THREE);
	BoolSupplier hasCrystal = mapInt(LocalBoard::getCrystal).gte(ConstInt.ONE);
	combat = ConstInt.SEVEN.when(hasCrystal);
    }
}
