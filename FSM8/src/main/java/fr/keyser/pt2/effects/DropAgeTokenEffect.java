package fr.keyser.pt2.effects;

import java.util.stream.Stream;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.Slot;
import fr.keyser.pt2.prop.IntSupplier;

public class DropAgeTokenEffect implements MonoEffect {

    private final IntSupplier ageCount;

    public DropAgeTokenEffect(IntSupplier ageCount) {
	this.ageCount = ageCount;
    }

    @Override
    public Stream<Target> targets(Slot source) {
	return source.getBoard().getUnits().map(dc -> new Target(dc.getPosition()));
    }

    @Override
    public void apply(Slot source, Card target) {
	target.addAge(ageCount.getValue());
    }

}
