package fr.keyser.pt.event;

import java.util.List;
import java.util.UUID;

import fr.keyser.pt.MetaCard;

public class PlayerDoDraftEvent extends PlayerEvent {

    private final List<MetaCard> toDraft;

    public PlayerDoDraftEvent(UUID player, List<MetaCard> toDraft) {
	super(player);
	this.toDraft = toDraft;
    }

    public List<MetaCard> getToDraft() {
        return toDraft;
    }

   
}
