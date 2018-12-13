package fr.keyser.pt.effects;

import fr.keyser.pt.BuildingLevel;
import fr.keyser.pt.DeployedCard;

public class UpgradeBuildingToLevel2 implements SelfEffect {

    public static final UpgradeBuildingToLevel2 INSTANCE = new UpgradeBuildingToLevel2();

    private UpgradeBuildingToLevel2() {

    }

    @Override
    public void apply(DeployedCard card) {
	card.getPlayer().buildings().filter(DeployedCard::isLevel1).forEach(b -> b.setLevel(BuildingLevel.LEVEL2));
    }
}
