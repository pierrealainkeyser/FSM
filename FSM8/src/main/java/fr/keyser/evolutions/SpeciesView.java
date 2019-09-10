package fr.keyser.evolutions;

import java.util.List;

public class SpeciesView {

    private final int fatLevel;

    private final int foodLevel;

    private final SpeciesId id;

    private final int population;

    private final int size;

    private final List<Card> traits;

    public SpeciesView(SpeciesId id, int population, int size, int foodLevel, int fatLevel, List<Card> traits) {
	this.id = id;
	this.population = population;
	this.size = size;
	this.foodLevel = foodLevel;
	this.fatLevel = fatLevel;
	this.traits = traits;
    }

    public int getFatLevel() {
	return fatLevel;
    }

    public int getFoodLevel() {
	return foodLevel;
    }

    public SpeciesId getId() {
	return id;
    }

    public int getPopulation() {
	return population;
    }

    public int getSize() {
	return size;
    }

    public List<Card> getTraits() {
	return traits;
    }

    @Override
    public String toString() {
	return "SpeciesView [" + (id != null ? "id=" + id + ", " : "") + "population=" + population + ", size=" + size + ", foodLevel="
	        + foodLevel + ", fatLevel=" + fatLevel + ", " + (traits != null ? "traits=" + traits : "") + "]";
    }
}
