package fr.keyser.pt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Card {

    private final BooleanValue mayCombat;

    private final IntValue combat;

    private final IntValue crystal;

    private final IntValue wood;

    private final IntValue food;

    private final GainValue gold;

    private final GainValue deploy;

    private final GainValue age;

    private final GainValue war;

    private final List<ScopedSpecialEffect> effects;

    Card(CardEssence<?> e) {
	this.food = e.food;
	this.wood = e.wood;
	this.crystal = e.crystal;
	
	this.combat = e.combat;
	this.mayCombat = e.mayCombat;
	
	this.gold = new GainValue(IntValue.ZERO, e.gold);
	this.war = new GainValue(e.warLegend, e.warGold);
	this.age = new GainValue(e.ageLegend, e.ageGold);
	this.deploy = new GainValue(e.deployLegend, e.deployGold);
	this.effects = e.effects == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(e.effects));
    }

    public final List<ScopedSpecialEffect> getEffects() {
	return effects;
    }

    public final IntValue getCombat() {
	return combat;
    }

    public final IntValue getCrystal() {
	return crystal;
    }

    public final IntValue getFood() {
	return food;
    }

    public final BooleanValue getMayCombat() {
	return mayCombat;
    }

    public final String getName() {
	return getClass().getSimpleName();
    }

    public final IntValue getWood() {
	return wood;
    }

    public GainValue getGold() {
	return gold;
    }

    public GainValue getDeploy() {
	return deploy;
    }

    public GainValue getAge() {
	return age;
    }

    public GainValue getWar() {
	return war;
    }
}
