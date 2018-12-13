package fr.keyser.pt.effects;

import java.util.Map;

import fr.keyser.pt.CardPosition;

public class ChoosenTargets {

    public ChoosenTargets() {

    }

    public ChoosenTargets(Map<String, CardPosition> raw) {

    }

    public CardPosition get(String key) {
	return null;
    }

    public CardPosition getDefault() {
	return get(Target.DEFAULT);
    }
}
