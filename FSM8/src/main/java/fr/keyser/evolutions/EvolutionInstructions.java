package fr.keyser.evolutions;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class EvolutionInstructions {
    private int index;

    private Set<CardId> newSpecies = new HashSet<>();

    private Set<CardId> population = new HashSet<>();

    private Set<CardId> size = new HashSet<>();

    private Map<Integer, CardId> traits = new HashMap<>();

    public Collection<CardId> discarded() {
	Set<CardId> all = new HashSet<>();
	all.addAll(newSpecies);
	all.addAll(population);
	all.addAll(size);
	return all;
    }

    public int getIndex() {
	return index;
    }

    public Set<CardId> getNewSpecies() {
	return newSpecies;
    }

    public Set<CardId> getPopulation() {
	return population;
    }

    public Set<CardId> getSize() {
	return size;
    }

    public Map<Integer, CardId> getTraits() {
	return traits;
    }

    public void setIndex(int index) {
	this.index = index;
    }

    public void setNewSpecies(Set<CardId> newSpecies) {
	this.newSpecies = newSpecies;
    }

    public void setPopulation(Set<CardId> aopulation) {
	this.population = aopulation;
    }

    public void setSize(Set<CardId> size) {
	this.size = size;
    }
}