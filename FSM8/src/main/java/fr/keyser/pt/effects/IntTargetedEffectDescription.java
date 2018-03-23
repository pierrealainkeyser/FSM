
package fr.keyser.pt.effects;

import java.util.stream.Stream;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.TargetedEffectDescription;

public class IntTargetedEffectDescription extends TargetedEffectDescription {

    private final int amount;

    public IntTargetedEffectDescription(String name, int amount, Stream<DeployedCard> target) {
	super(name, target);
	this.amount = amount;
    }

    public int getAmount() {
	return amount;
    }

}
