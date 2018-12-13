package fr.keyser.pt2;

import java.util.function.Function;

public final class BoardAccessor {
    private final Slot slot;

    public BoardAccessor(Slot slot) {
	this.slot = slot;
    }

    public <T> T board(Function<LocalBoard, T> accessor) {
	if (slot == null)
	    return null;
	return slot.get(accessor);
    }

}