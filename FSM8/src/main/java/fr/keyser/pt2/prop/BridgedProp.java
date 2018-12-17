package fr.keyser.pt2.prop;

import java.util.function.Function;

public class BridgedProp<T, R> extends ObservableSupplier<R> {

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

    public final R get() {
	if (actual != null)
	    return actual.get();
	else
	    return null;
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
}
