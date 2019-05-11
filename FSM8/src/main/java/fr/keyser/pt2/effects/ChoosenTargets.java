package fr.keyser.pt2.effects;

import java.util.Collections;
import java.util.Map;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt2.TargetableEffect;

public class ChoosenTargets {

    private final Map<String, CardPosition> raw;

    public ChoosenTargets() {
	this(Collections.emptyMap());
    }

    public ChoosenTargets(Map<String, CardPosition> raw) {
	this.raw = raw;

    }

    public CardPosition get(TargetableEffect effect, String key) {
	return raw.get(effect.name() + "-" + key);
    }

    public CardPosition get(TargetableEffect effect) {
	return raw.get(effect.name());
    }
}
