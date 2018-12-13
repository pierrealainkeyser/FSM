package fr.keyser.pt2.prop;

public class PlugableProp<T> extends ComputedProp<T> {

    private DirtySupplier<T> supplier;

    private T defaultValue;

    public PlugableProp(T defaultValue) {
	this.defaultValue = defaultValue;
    }

    public PlugableProp() {
	this((T) null);
    }

    public PlugableProp(T defaultValue, DirtySupplier<T> supplier) {
	this(defaultValue);
	setSupplier(supplier);
    }

    public PlugableProp(DirtySupplier<T> supplier) {
	this(null, supplier);
    }

    @Override
    protected T compute() {
	if (supplier != null)
	    return supplier.get();
	else
	    return defaultValue;
    }

    public void setSupplier(DirtySupplier<T> supplier) {
	if (this.supplier != null) {
	    this.supplier.removeListener(dirtyListener);
	}

	this.supplier = supplier;
	dirtyListener.setDirty();

	if (this.supplier != null) {
	    this.supplier.addListener(dirtyListener);
	}
    }
}
