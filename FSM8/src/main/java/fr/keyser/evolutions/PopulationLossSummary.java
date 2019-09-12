package fr.keyser.evolutions;

public final class PopulationLossSummary {
    private final int drawTraits;

    private final boolean extinct;

    private final int delta;

    private final int score;

    public PopulationLossSummary(int delta, boolean extinct, int drawTraits, int score) {
	this.delta = delta;
	this.extinct = extinct;
	this.drawTraits = drawTraits;
	this.score = score;
    }

    public int getDrawTraits() {
	return drawTraits;
    }

    public int getDelta() {
	return delta;
    }

    public int getScore() {
	return score;
    }

    public boolean isExtinct() {
	return extinct;
    }
}