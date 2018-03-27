package fr.keyser.pt.effects;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.PlayerBoard;
import fr.keyser.pt.TargetedEffectDescription;
import fr.keyser.pt.TargetedSpecialEffect;

public class PreserveFromDeathEffect implements TargetedSpecialEffect {

    public static final String SAVE = "save";

    @Override
    public List<TargetedEffectDescription> asyncEffect(DeployedCard source) {
	PlayerBoard player = source.getPlayer();
	boolean present = player.dyings().findFirst().isPresent();
	if (present)
	    return asList(new TargetedEffectDescription(SAVE, player.dyings()));
	else
	    return null;
    }

    @Override
    public void apply(DeployedCard source, Map<String, CardPosition> positions) {
	CardPosition position = positions.get(SAVE);
	if (position == null)
	    throw new IllegalStateException(SAVE + " position's not registered");

	source.getPlayer().preserveFromDeath(position);

    }

}
