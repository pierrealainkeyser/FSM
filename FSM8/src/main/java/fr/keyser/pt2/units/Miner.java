package fr.keyser.pt2.units;

import fr.keyser.pt2.prop.ConstInt;

public class Miner extends Unit {
    public Miner() {
	super(ConstInt.ONE);
	combat = ConstInt.TWO;
	crystal = ConstInt.ONE;
    }
}
