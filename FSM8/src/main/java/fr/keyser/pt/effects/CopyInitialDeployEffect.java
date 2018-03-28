package fr.keyser.pt.effects;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fr.keyser.pt.AsyncSpecialEffect;
import fr.keyser.pt.CardPosition;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.ScopedSpecialEffect;
import fr.keyser.pt.SpecialEffect;
import fr.keyser.pt.TargetedEffectDescription;
import fr.keyser.pt.TargetedSpecialEffect;

public class CopyInitialDeployEffect implements TargetedSpecialEffect {

    public static final String COPY = "copy";

    @Override
    public List<TargetedEffectDescription> asyncEffect(DeployedCard source) {

	Set<CardPosition> direct = new HashSet<>();
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
		direct.add(position);
		if (!asyncsEmpty) {
		    List<TargetedEffectDescription> subTargeted = asyncs.stream().flatMap(s -> s.asyncEffect(d).stream())
		            .collect(Collectors.toList());
		    positions.put(position, subTargeted);
		}
	    }
	});

	// TODO gestion des parametres
	return asList(new TargetedEffectDescription(COPY, new ArrayList<>(direct)));
    }

    @Override
    public void apply(DeployedCard source, Map<String, CardPosition> positions) {

	CardPosition position = positions.get(COPY);
	if (position == null)
	    throw new IllegalStateException(COPY + " position's not registered");
	
	
	DeployedCard target = source.find(position);

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