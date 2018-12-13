package fr.keyser.pt;

import java.util.function.Function;
import java.util.stream.Stream;

public abstract class Counters {

    private int combat;

    private int crystal;
    private int food;
    private int wood;

    private final Gain deploy = new Gain();

    private final Gain war = new Gain();

    private final Gain age = new Gain();

    private final Gain gold = new Gain();

    protected Counters() {

    }

    protected Counters(Counters counters) {
	this.combat = counters.combat;
	this.crystal = counters.crystal;
	this.food = counters.food;
	this.wood = counters.wood;

	deploy.setAll(Stream.of(counters.deploy));
	war.setAll(Stream.of(counters.war));
	age.setAll(Stream.of(counters.deploy));
	gold.setAll(Stream.of(counters.gold));
    }

    public void sumResources(Stream<CardCounters> str) {

	crystal = 0;
	food = 0;
	wood = 0;

	str.forEach(card -> {
	    crystal += card.getCrystal();
	    food += card.getFood();
	    wood += card.getFood();
	});

    }

    public void sumCombat(Stream<CardCounters> str) {
	combat = 0;
	str.forEach(card -> {
	    if (card.isMayCombat())
		combat += card.getCombat();
	});

    }

    public void sumAge(Stream<? extends Counters> cards) {
	sum(cards, Counters::getAge);
    }

    private void sum(Stream<? extends Counters> cards, Function<Counters, Gain> accessor) {
	accessor.apply(this).setAll(cards.map(accessor));
    }

    public void sumWar(Stream<? extends Counters> cards) {
	sum(cards, Counters::getWar);
    }

    public void sumDeploy(Stream<? extends Counters> cards) {
	sum(cards, Counters::getDeploy);
    }

    public void sumGold(Stream<? extends Counters> cards) {
	sum(cards, Counters::getGold);
    }

    public int getCombat() {
	return combat;
    }

    public void setCombat(int combat) {
	this.combat = combat;
    }

    public int getCrystal() {
	return crystal;
    }

    public void setCrystal(int crystal) {
	this.crystal = crystal;
    }

    public int getFood() {
	return food;
    }

    public void setFood(int food) {
	this.food = food;
    }

    public int getWood() {
	return wood;
    }

    public void setWood(int wood) {
	this.wood = wood;
    }

    public Gain getDeploy() {
	return deploy;
    }

    public Gain getWar() {
	return war;
    }

    public Gain getAge() {
	return age;
    }

    public Gain getGold() {
	return gold;
    }

}