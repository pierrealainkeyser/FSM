package fr.keyser.pt2.effects;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.LocalBoard;
import fr.keyser.pt2.Slot;

public interface MonoEffect extends TargetableEffect {

    public void apply(Slot source, Card target);

    @Override
    public default void apply(Slot source, ChoosenTargets targets) {
	LocalBoard board = source.getBoard();
	Slot slot = board.getSlot(targets.getDefault());
	Card target = slot.getCard().get();
	apply(source, target);
    }
}
