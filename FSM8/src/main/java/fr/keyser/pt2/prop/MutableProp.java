package fr.keyser.pt2.prop;

import java.util.Objects;

public class MutableProp<T> extends ObservableSupplier<T> {

    private T value;

    public MutableProp() {

    }

    public MutableProp(T value) {
	set(value);
    }

    @Override
    public T get() {
	return value;
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
