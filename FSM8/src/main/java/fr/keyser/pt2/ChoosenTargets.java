package fr.keyser.pt2;

import java.util.Collections;
import java.util.Map;

import fr.keyser.pt.CardPosition;

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
