package fr.keyser.pt2.prop;

import java.util.Objects;

public class MutableProp<T> implements DirtySupplier<T> {

    private final WeakDirtyObserver observer = new WeakDirtyObserver();

    private T value;

    public MutableProp() {

    }

    public MutableProp(T value) {
	set(value);
    }

    @Override
    public void addListener(DirtyListener e) {
	observer.addListener(e);

    }

    @Override
    public T get() {
	return value;
    }

    @Override
    public void removeListener(DirtyListener e) {
	observer.removeListener(e);

    }

    public void set(T value) {
	boolean changed = !Objects.equals(value, this.value);
	this.value = value;
	if (changed)
	    observer.setDirty();
    }

    @Override
    public String toString() {
	return Objects.toString(value);
    }
}
