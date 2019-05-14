package fr.keyser.pt2.units;

import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.PhaseEvent;
import fr.keyser.pt2.effects.DropAgeTokenEffect;
import fr.keyser.pt2.prop.ConstInt;

public class Alchimist extends Unit {
    public Alchimist() {
	super(ConstInt.TWO);
	addEffect(PhaseEvent.WHEN_DEPLOYED, new DropAgeTokenEffect(mapInt(LocalBoard::getFood)));
	payGoldGain = mapInt(LocalBoard::getWood);
	warLegend = mapInt(LocalBoard::getVictory).mult(mapInt(LocalBoard::getCrystal));
    }

}
