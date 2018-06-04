package fr.keyser.pt.event;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.TargetedEffectDescription;

public class PlayerInputActionEvent extends PlayerEvent {

    private final Map<CardPosition, List<TargetedEffectDescription>> actions;

    public PlayerInputActionEvent(UUID player, Map<CardPosition, List<TargetedEffectDescription>> actions) {
	super(player);
	this.actions = actions;
    }

    public Map<CardPosition, List<TargetedEffectDescription>> getActions() {
	return actions;
    }

}
