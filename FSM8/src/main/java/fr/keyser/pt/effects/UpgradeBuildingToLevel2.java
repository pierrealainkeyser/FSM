package fr.keyser.pt.effects;

import fr.keyser.pt.BuildingLevel;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.SpecialEffect;

public class UpgradeBuildingToLevel2 implements SpecialEffect {

    public static final SpecialEffect INSTANCE = new UpgradeBuildingToLevel2();

    private UpgradeBuildingToLevel2() {

    }

    @Override
    public void apply(DeployedCard card) {
	card.getPlayer().buildings().filter(DeployedCard::isLevel1).forEach(b -> b.setLevel(BuildingLevel.LEVEL2));
    }
}
