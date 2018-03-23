package fr.keyser.pt.effects;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.keyser.pt.AsyncSpecialEffect;
import fr.keyser.pt.CardPosition;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.ScopedSpecialEffect;
import fr.keyser.pt.SpecialEffect;
import fr.keyser.pt.TargetedEffectDescription;
import fr.keyser.pt.TargetedSpecialEffect;

public class CopyInitialDeployEffect implements TargetedSpecialEffect {

    private static final String COPY = "copy";

    @Override
    public List<TargetedEffectDescription> asyncEffect(DeployedCard source) {

	Map<CardPosition, List<TargetedEffectDescription>> positions = new HashMap<>();

	source.getPlayer().units().filter(d -> source != d).forEach(d -> {
	    Map<Boolean, List<ScopedSpecialEffect>> effects = d.intialDeployementEffect()
	            .collect(Collectors.partitioningBy(ScopedSpecialEffect::isAsync));
	    List<ScopedSpecialEffect> asyncs = effects.getOrDefault(true, emptyList());
	    List<ScopedSpecialEffect> syncs = effects.getOrDefault(false, emptyList());

	    // remove same effect to prevent recursion
	    asyncs.removeIf(s -> s.testTargeter(p -> p instanceof CopyInitialDeployEffect));

	    boolean asyncsEmpty = asyncs.isEmpty();
	    if (!(asyncsEmpty && syncs.isEmpty())) {
		CardPosition position = d.getPosition();
		if (asyncsEmpty) {
		    positions.put(position, null);
		} else {
		    List<TargetedEffectDescription> subTargeted = asyncs.stream().flatMap(s -> s.asyncEffect(d).stream())
		            .collect(Collectors.toList());
		    positions.put(position, subTargeted);
		}
	    }
	});

	return asList(new IntTargetedEffectDescription(COPY, 0, source.getPlayer().units()));
    }

    @Override
    public void apply(DeployedCard source, Map<String, CardPosition> positions) {

	DeployedCard target = source.find(positions.get(COPY));

	target.intialDeployementEffect().forEach(s -> {

	    SpecialEffect effect = s.getSpecialEffect();
	    if (effect instanceof AsyncSpecialEffect) {
		AsyncSpecialEffect a = (AsyncSpecialEffect) effect;
		a.apply(target, positions);
	    } else {
		effect.apply(source);
	    }
	});
    }

}
