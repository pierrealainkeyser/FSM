package fr.keyser.pt;

public final class BuildingEssence extends CardEssence<BuildingEssence> {

    final RawBuildingCost level1;

    final RawBuildingCost level2;

    BuildingEssence(RawBuildingCost level1, RawBuildingCost level2) {
	this.level1 = level1;
	this.level2 = level2;
    }

    @Override
    protected BuildingEssence getThis() {
	return this;
    }

  
}
