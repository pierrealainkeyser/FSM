package fr.keyser.pt2.effects;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.stream.Stream;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.EffectLog;
import fr.keyser.pt2.Slot;
import fr.keyser.pt2.Target;
import fr.keyser.pt2.prop.IntSupplier;

public class DropAgeTokenEffect implements MonoEffect {

    private final IntSupplier ageCount;

    public DropAgeTokenEffect(IntSupplier ageCount) {
	this.ageCount = ageCount;
    }

    @Override
    public Stream<Target> targets(Slot source) {
	if (ageCount.getValue() <= 0)
	    return Stream.empty();

	return source.getBoard().getUnits().map(dc -> target(dc.getPosition()));
    }

    @Override
    public List<EffectLog> apply(Slot source, Card target) {
	int value = ageCount.getValue();
	target.addAge(value);
	return asList(EffectLog.ageOther(source.getCard().get(), value, target));
    }

}
