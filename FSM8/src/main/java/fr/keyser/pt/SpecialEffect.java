package fr.keyser.pt;

public interface SpecialEffect {
    public void apply(DeployedCard card);

    public default String getName() {
	return getClass().getSimpleName();
    }
}
