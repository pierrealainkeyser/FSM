package fr.keyser.pt2.effects;

import java.util.Arrays;
import java.util.List;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.EffectLog;
import fr.keyser.pt2.Slot;
import fr.keyser.pt2.units.Unit;

public class ShapeShift implements SelfEffect {

    public static final SelfEffect INSTANCE = new ShapeShift();

    private ShapeShift() {

    }

    @Override
    public List<EffectLog> apply(Slot source) {

	Card card = source.getCard().get();
	Unit unit = card.getDeck().nextUnit();

	source.play(unit);
	return Arrays.asList(EffectLog.deploy(unit));
    }

}
