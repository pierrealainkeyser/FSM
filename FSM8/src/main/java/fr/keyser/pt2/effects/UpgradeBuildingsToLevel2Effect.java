package fr.keyser.pt2.effects;

import fr.keyser.pt2.Slot;

public class UpgradeBuildingsToLevel2Effect implements SelfEffect {

    public static final UpgradeBuildingsToLevel2Effect INSTANCE = new UpgradeBuildingsToLevel2Effect();

    private UpgradeBuildingsToLevel2Effect() {

    }

    @Override
    public void apply(Slot card) {
	card.getBoard().getBuildings().forEach(b -> b.setBuildingLevel(2));
    }
}
