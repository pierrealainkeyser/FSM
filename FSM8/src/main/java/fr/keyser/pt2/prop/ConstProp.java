package fr.keyser.pt2.prop;

import java.util.Objects;

public class ConstProp<T> implements DirtySupplier<T> {

    private final T value;

    public ConstProp(T value) {
	this.value = value;
    }

    @Override
    public void addListener(DirtyListener e) {

    }

    @Override
    public T get() {
	return value;
    }

    @Override
    public void removeListener(DirtyListener e) {

    }

    @Override
    public String toString() {
	return Objects.toString(value);
    }

}
