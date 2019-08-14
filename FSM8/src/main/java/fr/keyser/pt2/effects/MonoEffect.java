package fr.keyser.pt2.effects;

import java.util.Collections;
import java.util.List;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt2.Card;
import fr.keyser.pt2.ChoosenTargets;
import fr.keyser.pt2.EffectLog;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.Slot;
import fr.keyser.pt2.TargetableEffect;

public interface MonoEffect extends TargetableEffect {

    public List<EffectLog> apply(Slot source, Card target);

    @Override
    public default List<EffectLog> apply(Slot source, ChoosenTargets targets) {
	LocalBoard board = source.getBoard();
	CardPosition position = targets.get(this);
	if (position == null)
	    return Collections.emptyList();
	
	Slot slot = board.getSlot(position);
	Card target = slot.getCard().get();
	return apply(source, target);
    }
}
