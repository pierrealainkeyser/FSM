package fr.keyser.pt.event;

import java.util.List;
import java.util.UUID;

import fr.keyser.pt.BuildingConstruction;

public class PlayerBuildPlanEvent extends PlayerEvent {

    private final List<BuildingConstruction> buildPlan;

    public PlayerBuildPlanEvent(UUID player, List<BuildingConstruction> buildPlan) {
	super(player);
	this.buildPlan = buildPlan;
    }

    public List<BuildingConstruction> getBuildPlan() {
	return buildPlan;
    }

}
