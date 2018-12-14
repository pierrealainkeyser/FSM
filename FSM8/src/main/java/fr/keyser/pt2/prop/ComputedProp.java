package fr.keyser.pt2.prop;

import java.util.Collection;
import java.util.Objects;

public abstract class ComputedProp<T> extends ObservableSupplier<T> {

    private boolean dirty = true;

    protected final DirtyListener dirtyListener = this::setDirty;

    private T value;

    protected ComputedProp(Collection<? extends DirtyObserver> sources) {
	for (DirtyObserver dl : sources)
	    dl.addListener(dirtyListener);
    }

    protected ComputedProp(DirtyObserver... sources) {
	for (DirtyObserver dl : sources)
	    dl.addListener(dirtyListener);
    }

    protected abstract T compute();

    public final T get() {
	if (dirty) {
	    value = compute();
	    dirty = false;
	}
	return value;
    }

    private void setDirty() {
	if (!dirty) {
	    dirty = true;
	    observer.setDirty();
	}
    }

    @Override
    public String toString() {
	return Objects.toString(get());
    }

    public boolean isDirty() {
	return dirty;
    }

}
