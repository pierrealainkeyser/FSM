package fr.keyser.pt2;

import java.util.ArrayList;
import java.util.List;

import fr.keyser.pt.CardPosition;

public class EffectLog {

    public enum Effect {
	ADD_AGE, ADD_LEGEND, ADD_GOLD, UPGRADE, PRESERVE, DEPLOY, BUILD
    }

    public static EffectLog age(Card source, int delta) {
	return new EffectLog(source.getPosition(), Effect.ADD_AGE, delta, null);
    }

    public static EffectLog deploy(Card source) {
	return new EffectLog(source.getPosition(), Effect.DEPLOY, null, null);
    }

    public static EffectLog legend(Card source, int delta) {
	return new EffectLog(source.getPosition(), Effect.ADD_LEGEND, delta, null);
    }

    public static EffectLog ageOther(Card source, int delta, Card target) {
	return new EffectLog(source.getPosition(), Effect.ADD_AGE, delta, target.getPosition());
    }

    public static EffectLog gainGold(Card source, int delta) {
	return new EffectLog(source.getPosition(), Effect.ADD_GOLD, delta, null);
    }

    public static EffectLog preserve(Card source, Card target) {
	return new EffectLog(source.getPosition(), Effect.PRESERVE, null, target.getPosition());
    }

    public static EffectLog upgrade(Card source, Card target) {
	return new EffectLog(source.getPosition(), Effect.UPGRADE, null, target.getPosition());
    }

    private final CardPosition source;

    private final Effect effect;

    private final Integer delta;

    private final CardPosition target;

    private final List<EffectLog> inner = new ArrayList<>();

    private EffectLog(CardPosition source, Effect effect, Integer delta, CardPosition target) {
	this.source = source;
	this.effect = effect;
	this.delta = delta;
	this.target = target;
    }

    public CardPosition getSource() {
	return source;
    }

    public Effect getEffect() {
	return effect;
    }

    public Integer getDelta() {
	return delta;
    }

    public CardPosition getTarget() {
	return target;
    }

    public List<EffectLog> getInner() {
	return inner;
    }

    @Override
    public String toString() {
	return "EffectLog [source=" + source + ", effect=" + effect + ", delta=" + delta + ", target=" + target + ", inner=" + inner + "]";
    }
}
