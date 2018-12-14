package fr.keyser.pt2.prop;

import java.util.Objects;
import java.util.function.Function;

public class BridgedProp<T, R> implements DirtySupplier<R> {
    private WeakDirtyObserver observer = new WeakDirtyObserver();

    private final Function<T, ? extends DirtySupplier<R>> mapper;

    private final DirtySupplier<T> source;

    private DirtySupplier<R> actual;

    protected final DirtyListener dirtyListener = observer::setDirty;

    public BridgedProp(DirtySupplier<T> source, Function<T, ? extends DirtySupplier<R>> mapper) {
	source.addListener(this::reload);
	this.source = source;
	this.mapper = mapper;
	reload();
    }

    @Override
    public final void addListener(DirtyListener e) {
	observer.addListener(e);
    }

    public final R get() {
	if (actual != null)
	    return actual.get();
	else
	    return null;
    }

    @Override
    public final void removeListener(DirtyListener e) {
	observer.removeListener(e);
    }

    private void reload() {
	if (actual != null)
	    actual.removeListener(dirtyListener);

	T t = source.get();
	actual = t == null ? null : mapper.apply(t);

	if (actual != null)
	    actual.addListener(dirtyListener);

	dirtyListener.setDirty();
    }

    @Override
    public String toString() {
	return Objects.toString(get());
    }

}
