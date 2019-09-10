package fr.keyser.evolutions;

public final class PopulationLossSummary {
    private final int drawTraits;

    private final boolean extinct;

    private final int populationLoss;

    private final int score;

    public PopulationLossSummary(int populationLoss, boolean extinct, int drawTraits, int score) {
        this.populationLoss = populationLoss;
        this.extinct = extinct;
        this.drawTraits = drawTraits;
        this.score = score;
    }

    public int getDrawTraits() {
        return drawTraits;
    }

    public int getPopulationLoss() {
        return populationLoss;
    }

    public int getScore() {
        return score;
    }

    public boolean isExtinct() {
        return extinct;
    }
}