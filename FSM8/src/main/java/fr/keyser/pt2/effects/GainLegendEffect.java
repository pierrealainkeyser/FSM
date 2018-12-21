package fr.keyser.pt2.effects;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.stream.Stream;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.EffectLog;
import fr.keyser.pt2.Slot;
import fr.keyser.pt2.Target;
import fr.keyser.pt2.prop.IntSupplier;

public class GainLegendEffect implements SelfEffect {

    private final IntSupplier legend;

    public GainLegendEffect(IntSupplier legend) {
	this.legend = legend;
    }

    @Override
    public Stream<Target> targets(Slot source) {
	if (legend.getValue() <= 0)
	    return Stream.empty();

	return SelfEffect.super.targets(source);
    }

    @Override
    public List<EffectLog> apply(Slot slot) {
	Card card = slot.getCard().get();
	int value = legend.getValue();
	card.getDeployLegend().add(value);
	return asList(EffectLog.legend(card, value));
    }
}
