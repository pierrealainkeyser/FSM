package fr.keyser.pt2.effects;

import fr.keyser.pt2.Slot;

public class UpgradeBuildingsToLevel2 implements SelfEffect {

    public static final UpgradeBuildingsToLevel2 INSTANCE = new UpgradeBuildingsToLevel2();

    private UpgradeBuildingsToLevel2() {

    }

    @Override
    public void apply(Slot card) {
	card.getBoard().getBuildings().forEach(b -> b.setBuildingLevel(2));
    }
}
