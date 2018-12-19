package fr.keyser.pt2.effects;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.stream.Stream;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.Slot;
import fr.keyser.pt2.prop.IntSupplier;

public class GainGoldEffect implements SelfEffect {

    private final IntSupplier gold;

    public GainGoldEffect(IntSupplier gold) {
	this.gold = gold;
    }

    @Override
    public Stream<Target> targets(Slot source) {
	if (gold.getValue() <= 0)
	    return Stream.empty();

	return SelfEffect.super.targets(source);
    }

    @Override
    public List<EffectLog> apply(Slot slot) {
	Card card = slot.getCard().get();
	int value = gold.getValue();
	card.getDeployGoldGain().add(value);
	return asList(EffectLog.gainGold(card, value));
    }
}
