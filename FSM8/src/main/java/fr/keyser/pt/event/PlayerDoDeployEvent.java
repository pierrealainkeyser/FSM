package fr.keyser.pt.event;

import java.util.List;
import java.util.UUID;

import fr.keyser.pt.MetaCard;

public class PlayerDoDeployEvent extends PlayerEvent {

    private final List<MetaCard> toDeploy;

    public PlayerDoDeployEvent(UUID player, List<MetaCard> toDeploy) {
	super(player);
	this.toDeploy = toDeploy;
    }

    public List<MetaCard> getToDeploy() {
        return toDeploy;
    }

   
}
