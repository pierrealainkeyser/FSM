package fr.keyser.evolutions;

import java.util.HashSet;
import java.util.Set;

public final class AttackInstructions {

    private SpeciesId attacker;

    private SpeciesId victim;

    private Set<CardId> damageReduction = new HashSet<>();

    private Set<CardId> traitAvoidances = new HashSet<>();

    public SpeciesId getAttacker() {
	return attacker;
    }

    public void setAttacker(SpeciesId attacker) {
	this.attacker = attacker;
    }

    public SpeciesId getVictim() {
	return victim;
    }

    public void setVictim(SpeciesId victim) {
	this.victim = victim;
    }

    public Set<CardId> getDamageReduction() {
	return damageReduction;
    }

    public void setDamageReduction(Set<CardId> damageReduction) {
	this.damageReduction = damageReduction;
    }

    public Set<CardId> getTraitAvoidances() {
	return traitAvoidances;
    }

    public void setTraitAvoidances(Set<CardId> traitAvoidances) {
	this.traitAvoidances = traitAvoidances;
    }
}
